package obpro.cui;

/**
 * Class Random.
 * TODO ������J�� �p�b�P�[�W���t�@�N�^�����O
 * @author macchan
 * @version $Id: Random.java,v 1.2 2007/06/13 17:54:01 camei Exp $
 */
public class Random {

	private static final java.util.Random random = new java.util.Random();

	/**
	 * �����_���Ȑ����𐶐�����	 
	 */
	public static int getInt(int n) {
		return random.nextInt(n);
	}
}
