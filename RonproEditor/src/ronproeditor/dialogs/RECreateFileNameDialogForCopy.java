/*
 * RECreateFileNameDialogForCopy.java
 * Created on 2011/11/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ronproeditor.dialogs;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import ronproeditor.REApplication;
import ronproeditor.RESourceManager;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;

/**
 * @author macchan
 */
public class RECreateFileNameDialogForCopy extends RERefactoringFileNameDialog {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> combobox = new JComboBox<String>();

	public RECreateFileNameDialogForCopy(REApplication application) {
		super(application);
		initializeComboBox();
	}

	private void initializeComboBox() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Project:"));
		combobox.setPreferredSize(new Dimension(150, 27));
		panel.add(combobox);
		getContentPane().add(panel);
		combobox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				validCheck();
			}
		});

		pack();
	}

	protected void validCheck() {
		String text = nameTextField.getText();
		if (text.length() == 0) {
			messageLabel.setText("�G���[�F���O����͂��Ă��������D");
			okButton.setEnabled(false);
		} else if (Character.isLowerCase(text.charAt(0))) {
			messageLabel.setText("�G���[�F�啶���ŊJ�n���Ă��������D");
			okButton.setEnabled(false);
		} else if (!getApplication().getSourceManager().canCreateFile(
				getInputtedProject(), text)) {
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

	public void open() {
		combobox.removeAllItems();
		RESourceManager manager = getApplication().getSourceManager();
		for (CDirectory dir : manager.getAllProjects()) {
			combobox.addItem(dir.getNameByString());
		}
		CDirectory current = manager.getCCurrentProject();
		combobox.setSelectedItem(current.getNameByString());
		super.open();
	}

	public CPath getInputtedProject() {
		String name = (String) combobox.getSelectedItem();
		CDirectory dir = getApplication().getSourceManager()
				.getCRootDirectory();
		return dir.getAbsolutePath().appendedPath(name);
	}
}
