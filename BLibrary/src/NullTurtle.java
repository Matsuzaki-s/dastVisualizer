/*
 * �v���O�������F
 * �쐬�ҁF 
 * �o�[�W�����F 1.0 (���t)
 */
public class NullTurtle extends CardTurtle {

	public static final NullTurtle NULL_TURTLE = new NullTurtle();

	private NullTurtle() {
		super("Null");
	}

	public void text(Object text) {
		// �������Ȃ�
		System.err.println("NullTurtle#text() �s���ȑ���ł��D");
	}

	public void hide() {
		// �������Ȃ�
		System.err.println("NullTurtle#hide() �s���ȑ���ł��D");
	}
}
