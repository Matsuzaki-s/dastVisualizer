package ronproeditor.ext.rss;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ronproeditor.dialogs.REDialog;
import ronproeditor.ext.REGeneRefManager;

public class RSEmptyTextDialog extends REDialog {

	private static final long serialVersionUID = 1L;

	// Dialog Size
	private final int WIDTH = 300;
	private final int HEIGHT = 110;

	private JButton okButton = new JButton("OK");

	public RSEmptyTextDialog(REGeneRefManager manager) {
		super(manager.getApplication().getFrame(), manager.getApplication());
		initializeDialog();
		initializeViews();
	}

	private void initializeDialog() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setResizable(false);
	}

	private void initializeViews() {

		// �{�^���쐬
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okFinish();
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.add(new JLabel(
				"<html>���Ȃ��L�q���Ă��Ȃ��G���[������܂��I<br>�S�ẴG���[�ɂ��ē��Ȃ��L�q���Ă�������</html>"));
		mainPanel.add(okButton);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	public void open() {
		setWindowAtCenter();
		setVisible(true);
	}

	private void close() {
		setVisible(false);
	}

	private void okFinish() {
		close();
	}
}
