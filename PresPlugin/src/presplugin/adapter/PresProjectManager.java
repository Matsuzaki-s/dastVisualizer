package presplugin.adapter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import pres.core.IPRRecordingProject;
import pres.core.PRNullRecordingProject;
import pres.core.PRRecordingProject;
import pres.core.PRThreadRecordingProject;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;

public class PresProjectManager {

	private Map<String, IPRRecordingProject> rprojects;

	private static final IPRRecordingProject NULL_PROJECT = new PRNullRecordingProject();

	public PresProjectManager() {
		initialize();
	}

	public void initialize() {
		rprojects = new LinkedHashMap<String, IPRRecordingProject>();
	}

	public void terminate() {
		try {
			for (IPRRecordingProject rproject : rprojects.values()) {
				rproject.checkTargetFilesAndUpdate();
				rproject.stop();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void refresh() {
		try {
			for (Iterator<IPRRecordingProject> i = rprojects.values()
					.iterator(); i.hasNext();) {
				IPRRecordingProject rproject = i.next();
				if (!rproject.valid()) {
					rproject.stop();
					i.remove();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized IPRRecordingProject getRecordingProject(IFile file) {
		try {
			refresh();
			IProject project = file.getProject();
			String pjName = project.getName();
			if (!rprojects.containsKey(pjName)) {
				IPRRecordingProject newRProject = createNewRecordingProject(project);
				if (newRProject.valid()) {
					newRProject.start();
					newRProject.checkTargetFilesAndUpdate();
					rprojects.put(pjName, newRProject);
				}
			}
			IPRRecordingProject rProject = rprojects.get(pjName);
			if (rProject == null) {
				return NULL_PROJECT;
			}
			return rProject;

		} catch (Exception ex) {
			ex.printStackTrace();
			return NULL_PROJECT;
		}
	}

	private IPRRecordingProject createNewRecordingProject(IProject project) {
		// 2013.12.16 前のやり方．これだとgitフォルダへ移行したとき問題がある．
		// IPath projectPath = project.getFullPath();
		// IPath workspacePath = project.getWorkspace().getRoot().getLocation();
		// IPath projectFullPath = workspacePath.append(projectPath);
		// CDirectory base = CFileSystem.findDirectory(projectFullPath.toString());

		// 2013.12.16 新しいやり方．簡単なAPIができた
		IPath projectFullPath = project.getLocation();
		CDirectory base = CFileSystem.findDirectory(projectFullPath.toString());

		PRRecordingProject rproject = new PRRecordingProject(base);
		rproject.setDirFilter(CFileFilter.IGNORE_BY_NAME_FILTER(".*", "CVS",
				"bin"));
		rproject.setFileFilter(CFileFilter.ACCEPT_BY_NAME_FILTER("*.java",
				"*.hcp", "*.c", "*.cpp", "Makefile", "*.oil", "*.rb", "*.bat",
				"*.tex"));
		return new PRThreadRecordingProject(rproject);
	}

}
