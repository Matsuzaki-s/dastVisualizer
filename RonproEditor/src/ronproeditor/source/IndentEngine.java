package ronproeditor.source;

import java.util.Scanner;

import ronproeditor.helpers.FileSystemUtil;

/**
 * �@�\�[�X�R�[�h���`������G���W���ł��B
 */
public class IndentEngine {

	/**
	 * �@��s���ǂ݁A�X�L�������Ă����܂��B
	 */
	public static String execIndent(String before) {

		// 2011/11/20�ǋL macchan
		while (before.contains("}}")) {
			before = before.replace("}}", "}\n}");
		}
		while (before.contains("{{")) {
			before = before.replace("{{", "{\n{");
		}

		Scanner scanner = new Scanner(before);
		StringBuffer buf = new StringBuffer();

		try {
			int braceCount = 0;// ���݂̃^�u�̐����o���Ă���
			boolean inComment = false;// �R�����g�����ǂ���(���)
			while (scanner.hasNext()) {

				// ��s�Ƃ��Ă���
				String lineString = scanner.nextLine();

				// �X�L��������
				ScanResult sr = IndentUtil.scanLine(lineString, inComment);
				inComment = sr.getInComment();

				// ����ꍇ��Ɍ��炷
				if (sr.getCloseBraceCount() > 0) {
					braceCount = braceCount - sr.getCloseBraceCount();
				}

				// �X�L���������s�̃C���f���g���킹������
				StringBuffer linebuf = new StringBuffer(lineString);
				IndentUtil.doIndentLine(braceCount + sr.getCloseSecond(),
						linebuf);
				buf.append(linebuf.toString());
				if (scanner.hasNext()) {
					buf.append(FileSystemUtil.CR);
				}

				// ������ꍇ���Ƃɑ��₷
				if (sr.getOpenBraceCount() > 0) {
					braceCount = braceCount + sr.getOpenBraceCount();
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		scanner.close();
		return buf.toString();
	}

}
