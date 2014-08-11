/*
 * RECreateProjectNameDialog.java
 * Created on 2007/09/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import ronproeditor.REApplication;

/**
 * RECreateProjectNameDialog
 */
public class RECreateProjectNameDialog extends RECreateNameDialog {

	private static final long serialVersionUID = 1L;

	public RECreateProjectNameDialog(REApplication application) {
		super(application);
		setTitle("�V�K�v���W�F�N�g�쐬");
	}

	protected void validCheck() {
		String text = nameTextField.getText();
		if (text.length() == 0) {
			messageLabel.setText("�G���[�F���O����͂��Ă��������D");
			okButton.setEnabled(false);
		} else if (!getApplication().getSourceManager().canCreateProject(text)) {
			messageLabel.setText("�G���[�F���̖��O�̃v���W�F�N�g�͂��łɑ��݂��܂��D");
			okButton.setEnabled(false);
		} else if (!isValidCharacterUsed(text)) {
			messageLabel.setText("�G���[�FJava�ŗ��p�ł��Ȃ��������܂܂�Ă��܂��D");
			okButton.setEnabled(false);
		} else {
			messageLabel.setText("�@");
			okButton.setEnabled(true);
		}
	}

	@Override
	public String getDefaultName() {
		String basename = "NewProject";
		String name = basename;
		for (int i = 2; i < 10; i++) {
			if (getApplication().getSourceManager().canCreateProject(name)) {
				break;
			}
			name = basename + i;
		}
		return name;
	}

	@Override
	protected String getInputTitle() {
		return "�v���W�F�N�g������͂��Ă�������";
	}

}
