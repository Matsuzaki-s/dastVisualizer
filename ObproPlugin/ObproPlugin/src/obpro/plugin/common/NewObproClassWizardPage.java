/*
 * Created on 2007/06/13
 *
 * Copyright (c) 2007 camei.  All rights reserved.
 */
package obpro.plugin.common;

import java.net.URL;

import obpro.plugin.ObproPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * �I�u�v���pJava�N���X�����p�E�B�U�[�h
 * 
 * @author camei
 * @version $Id: NewObproClassWizardPage.java,v 1.1 2007/06/13 19:16:01 camei
 *          Exp $
 */
public abstract class NewObproClassWizardPage extends NewClassWizardPage {

	public static final String TEMPLATE_COMMENT = "template/comment";

	public static final String PROJECT_ERROR_MSG = "�I�u�v���v���W�F�N�g�łȂ��̂ŁC�N���X���쐬�ł��܂���D�v���W�F�N�g����蒼���Ă�������";

	protected StatusInfo isObproProject = new StatusInfo();

	/**
	 * @return �{���e���v���[�g��URL
	 */
	protected abstract String getTemplatePath();

	/**
	 * @return �C���|�[�g��
	 */
	protected abstract String getImportText();

	/**
	 * @return �N���X�R�����g
	 */
	protected String getTypeComment() {
		Template template = new Template();
		URL commentURL = ObproPlugin.getDefault().getURL(TEMPLATE_COMMENT);
		String comment = template.generate(commentURL);
		return comment;
	}

	/**
	 * File�R�����g�͓f���Ȃ��悤�ɃI�[�o�[���C�h
	 */
	protected String getFileComment(ICompilationUnit parentCU,
			String lineDelimiter) throws CoreException {
		return null;
	}

	/**
	 * Type�R�����g���I�u�v���`���ɃI�[�o�[���C�h
	 */
	protected String getTypeComment(ICompilationUnit parentCU,
			String lineDelimiter) {
		return getImportText() + lineDelimiter + getTypeComment();
	}

	/**
	 * �N���X�̃����o���쐬 ���g�i�R���X�g���N�^�Amain�A�t�B�[���h�j�͂����őS������Ă��܂����Ƃɂ���B
	 */
	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		Template template = new Template();

		// �ϐ��l�̐ݒ�
		String className = type.getElementName();
		template.setVariable("className", className);
		template
				.setVariable("instanceName", Template.toInstanceName(className));

		// �e���v���[�g����{������
		URL contentURL = ObproPlugin.getDefault().getURL(getTemplatePath());
		String content = template.generate(contentURL);

		// �{����ݒ肷��
		type.createMethod(content, null, false, null);
	}

	// ------ �o���f�[�V�����ύX�̂��߂̃I�[�o�[���C�h --------
	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		super.init(selection);

		// if (!ObproPlugin.isObproProject(getJavaProject())) {
		// IStatus status = new Status(IStatus.ERROR, ObproPlugin.PLUGIN_ID,
		// PROJECT_ERROR_MSG);
		// ErrorDialog.openError(getShell(), null, null, status);
		// throw new RuntimeException("Is Not ObproProject");
		// }

		doStatusUpdate();
	}

	/*
	 * @see NewContainerWizardPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		doStatusUpdate();
	}

	private void doStatusUpdate() {
		// �I�u�v�����C�u�����`�F�b�N
		if (!ObproPlugin.isObproProject(getJavaProject())) {
			isObproProject.setError(PROJECT_ERROR_MSG);
		}

		// ����������͂��܂�N���X���͋����Ȃ��D
		StatusInfo typeNameStatus = ((StatusInfo) fTypeNameStatus);
		if (typeNameStatus.isWarning()) {
			typeNameStatus.setError(typeNameStatus.getMessage());
		}

		// status of all used components
		IStatus[] status = new IStatus[] {
				fContainerStatus,
				isObproProject, // ���ǉ�
				// �������̓f�t�H���g�p�b�P�[�W�ł��n�j�ɂ���̂Ŗ������D
				// isEnclosingTypeSelected() ? fEnclosingTypeStatus :
				// fPackageStatus,
				fTypeNameStatus, fModifierStatus, fSuperClassStatus,
				fSuperInterfacesStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);

	}
}
