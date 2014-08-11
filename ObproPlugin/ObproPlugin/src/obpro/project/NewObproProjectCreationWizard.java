/*
 * NewObproProjectCreationWizard.java
 * Created on 2007/04/30 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import obpro.plugin.ObproPlugin;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * NewObproProjectCreationWizard
 */
public class NewObproProjectCreationWizard extends JavaProjectWizard {

	/**
	 * Constructor for NewObproProjectCreationWizard.
	 */
	public NewObproProjectCreationWizard() {
		super();

		this.setWindowTitle("�I�u�v���v���W�F�N�g�̍쐬");
		this.setDefaultPageImageDescriptor(ObproPlugin
				.getImageDescriptor("icons/icon_ObproProject.gif"));
		// this.setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
	}

	/***************************************************************************
	 * Override Methods
	 **************************************************************************/

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {

		// create project
		boolean result = super.performFinish();

		// setup obpro.jar
		if (result) {
			try {
				IWorkspaceRunnable op = new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor)
							throws CoreException, OperationCanceledException {
						monitor.beginTask("setup " + ObproPlugin.OBPRO_JAR, 1);
						bindTurtleJar();
						monitor.worked(1);
					}
				};
				Job job = Job.getJobManager().currentJob();
				ISchedulingRule rule = null;
				if (job != null)
					rule = job.getRule();
				IRunnableWithProgress runnable = null;
				if (rule != null)
					runnable = new WorkbenchRunnableAdapter(op, rule, true);
				else
					runnable = new WorkbenchRunnableAdapter(op,
							getSchedulingRule());
				getContainer().run(canRunForked(), true, runnable);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		return result;
	}

	/***************************************************************************
	 * Set up Jars
	 **************************************************************************/

	/**
	 * Obpro.jar���o�C���h����
	 */
	private void bindTurtleJar() {
		try {
			List<IClasspathEntry> libraries = getCurrentClasspathEntries();
			IClasspathEntry turtleCpEntry = JavaCore.newVariableEntry(new Path(
					ObproPlugin.OBPRO_JAR_PATH_VARIABLE), null, null);
			libraries.add(turtleCpEntry);
			this.getJavaProject().setRawClasspath(
					libraries.toArray(new IClasspathEntry[libraries.size()]),
					null);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	/**
	 * �����炠��N���X�p�X�����̂܂܎擾����(�����ɒǉ����邽��)
	 * 
	 * @return List<IClasspathEntry> Object
	 */
	private List<IClasspathEntry> getCurrentClasspathEntries()
			throws JavaModelException {
		List<IClasspathEntry> jars = new ArrayList<IClasspathEntry>();
		IClasspathEntry[] entries = this.getJavaProject().getRawClasspath();
		jars.addAll(Arrays.asList(entries));
		return jars;
	}

	// Java�v���W�F�N�g���擾����
	private IJavaProject getJavaProject() {
		IJavaProject javaProject = ((NewJavaProjectWizardPageTwo) getPages()[1])
				.getJavaProject();
		return javaProject;
	}

	// Turtle.jar�̂��肩���擾����
	// private IPath getTurtleJarPath() {
	// IPath pluginPath = ObproPlugin.getDefault().getStateLocation();
	// IPath turtleJarPath = pluginPath.append(ObproPlugin.OBPRO_JAR);
	// return turtleJarPath;
	// }

}
