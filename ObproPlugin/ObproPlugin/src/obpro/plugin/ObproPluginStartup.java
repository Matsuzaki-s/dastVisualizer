package obpro.plugin;

import org.eclipse.ui.IStartup;

public class ObproPluginStartup implements IStartup {

	public void earlyStartup() {
		// ���̃N���X�����[�h���ꂽ���_�� ObproPlugin#start ���Ă΂��̂ŁA
		// ���̃��\�b�h�̒��g�͋�ɂ��Ă����āA ObproPlugin#start �� updateLibrary �������Ă�
		// ���͂Ȃ����A�Ӗ��I�ɂ����ɋL�q���Ă����B
		try {
			ObproPlugin.getDefault().updateLibrary();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
