package app.jws;

import framework.DnDFramework;

public class JWSCreaterMain {

	public static void main(String[] args) {
		DnDFramework.open("JWSCreater", "�ԍ������ꂽ�t�H���_���X�g���h���b�v���Ă�������",
				new JWSCreaterStrategy());
	}
}
