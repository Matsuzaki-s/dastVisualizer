/*
 * RECreateFileNameDialog.java
 * Created on 2007/09/18 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import ronproeditor.REApplication;

/**
 * RECreateFileNameDialog
 */
public class RECreateFileNameDialog extends RECreateNameDialog {

	private static final long serialVersionUID = 1L;

	public RECreateFileNameDialog(REApplication application) {
		super(application);
		setTitle("�V�K�t�@�C���i�N���X�j�쐬");
	}

	protected void validCheck() {
		String text = nameTextField.getText();
		if (text.length() == 0) {
			messageLabel.setText("�G���[�F���O����͂��Ă��������D");
			okButton.setEnabled(false);
		} else if (Character.isLowerCase(text.charAt(0))) {
			messageLabel.setText("�G���[�F�啶���ŊJ�n���Ă��������D");
			okButton.setEnabled(false);
		} else if (!getApplication().getSourceManager().canCreateFile(text)) {
			messageLabel.setText("�G���[�F���̖��O�̃t�@�C���͂��łɑ��݂��܂��D");
			okButton.setEnabled(false);
		} else if (!isValidFirstCharacterUsed(text)) {
			messageLabel.setText("�G���[�F�ŏ��̕�����Java�ŗ��p�ł��Ȃ��������܂܂�Ă��܂��D");
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
		String basename = "NewClass";
		String name = basename;
		for (int i = 2; i < 10; i++) {
			if (getApplication().getSourceManager().canCreateFile(name)) {
				break;
			}
			name = basename + i;
		}
		return name;
	}

	@Override
	protected String getInputTitle() {
		return "�N���X���i.java�����������O�j����͂��Ă�������";
	}

}
