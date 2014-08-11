package ronproeditor.source;

/**
 *�@�R�[�h�𐮌`���邽�߂֗̕����\�b�h�������Ă���N���X�ł��B
 */
public class IndentUtil {

	//	/**
	//	 * ���̍s�̌��݂̃C���f���g�̐��𐔂��郁�\�b�h�ł��B
	//	 */
	//	public static int countIndentDepth(String lineString) {
	//
	//		char[] lineChar = lineString.toCharArray();
	//		int countIndent = 0;
	//
	//		//�^�u�������̓X�y�[�X�̐��𐔂���
	//		for (int j = 0; j < lineChar.length; j++) {
	//			if (lineChar[j] == ' ') {
	//				countIndent++;
	//			} else if (lineChar[j] == '\t') {
	//				countIndent += REApplication.WHITESPACE_COUNT_FOR_TAB * 2;
	//			} else {
	//				return countIndent;
	//			}
	//		}
	//
	//		return countIndent;
	//	}

	/**
	 * ��s�𐳂����C���f���g�ɂ��郁�\�b�h�ł��B
	 */
	public static void doIndentLine(int depth, StringBuffer line) {
		int pos = getStartPoint(line);
		String lineString = line.toString();
		if (pos <= line.length()) {
			lineString = lineString.substring(pos);
		}
		if (line.length() > 0) {
			line.delete(0, line.length());
		}
		for (int j = 0; j < depth; j++) {
			line.append('\t');
		}
		line.append(lineString);

		//		int beforeIndentCount = countIndentDepth(lineString);
		//		int wishIndentCount = depth * REApplication.WHITESPACE_COUNT_FOR_TAB
		//				* 2;
		//		//�����Ă邩���ׂĂ�
		//		int insertSpace = wishIndentCount - beforeIndentCount;
		//		if (insertSpace == 0) {//���傤�ǂ���
		//			return;
		//		} else if (insertSpace > 0) {//�����Ȃ����炽��
		//			String insertString = "";
		//			for (int i = 0; i < insertSpace; i++) {
		//				insertString = insertString + " ";
		//			}
		//			line.insert(0, insertString);
		//		} else {//������������
		//			//�������I�}�C�i�X�ɂȂ�ƍ���̂ŏ������ق����Ƃ�
		//			insertSpace = insertSpace * -1;
		//			insertSpace = (insertSpace < beforeIndentCount) ? insertSpace
		//					: beforeIndentCount;
		//			for (int i = 0; i < insertSpace; i++) {
		//				line.deleteCharAt(0);
		//			}
		//		}
	}

	private static int getStartPoint(StringBuffer line) {
		int i;
		for (i = 0; i < line.length(); i++) {
			switch (line.charAt(i)) {
			case ' ':
			case '�@':
			case '\t':
				continue;
			default:
				return i;
			}
		}
		return i;
	}

	/**
	 * ��s�X�L�������郁�\�b�h�ł��B
	 */
	public static ScanResult scanLine(String s, boolean inComment) {
		ScanResult sr = new ScanResult();
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		char lastChar = '\0';
		char[] line = s.toCharArray();
		int len = line.length;

		for (int i = 0; i < len; i++) {
			if (line[i] == '}' && inSingleQuote == false
					&& inDoubleQuote == false && inComment == false) {
				sr.addCloseBraceCount();
			} else if (line[i] == '{' && inSingleQuote == false
					&& inDoubleQuote == false && inComment == false) {
				sr.addOpenBraceCount();
			} else if (line[i] == '/' && lastChar == '/'
					&& inDoubleQuote == false && inComment == false) {//�s�R�����g
				sr.setInComment(inComment);
				return sr;
			} else if (line[i] == '\'' && lastChar != '\\'
					&& inDoubleQuote == false && inComment == false) {//char���e����
				inSingleQuote = !inSingleQuote;
			} else if (line[i] == '"' && lastChar != '\\'
					&& inSingleQuote == false && inComment == false) {//�����񃊃e����
				inDoubleQuote = !inDoubleQuote;
			} else if (line[i] == '*' && lastChar == '/'
					&& inDoubleQuote == false && inComment == false) {//�R�����g�J���Ƃ�
				inComment = !inComment;
				lastChar = '\0';
				continue;
			} else if (line[i] == '/' && lastChar == '*'
					&& inDoubleQuote == false && inComment == true) {//�R�����g����Ƃ�
				inComment = !inComment;
				lastChar = '\0';
				continue;
			} else if (line[i] == '\\' && lastChar == '\\'
					&& (inSingleQuote == true || inDoubleQuote == true)) {//\\�͈�Ƃ݂Ȃ�
				lastChar = '\0';
				continue;
			}
			lastChar = line[i];
		}
		sr.setInComment(inComment);
		return sr;
	}
}