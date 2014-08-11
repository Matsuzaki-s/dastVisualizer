package ronproeditor.helpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	/**
	 * targetFiles��ZIP���k����zipFile�ɏo�͂���B
	 * 
	 * @param zipFile
	 *            ZIP�o�̓t�@�C����
	 * @param targetFiles
	 *            ���̓t�@�C����(�f�B���N�g��)�z��
	 * @throws IOException
	 *             ���o�͗�O
	 */
	public static void createZip(String zipFile, String targetDir ) throws IOException {
		createZip( zipFile, new String[]{ targetDir } );
	}
	public static void createZip(String zipFile, String[] targetFiles)
			throws IOException {

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		
//		if( OSUtil.isWindows() )
//			out.setEncoding("MS932"); 
//		else if( OSUtil.isMac() )
//			out.setEncoding("UTF-8");
		
		for (int i = 0; i < targetFiles.length; i++) {
			File file = new File(targetFiles[i]);
			createZip(out, file);
		}
		out.close();
	}

	/**
	 * targetFile��out��ZIP�G���g���֏o�͂���B
	 * 
	 * @param out
	 *            ZIP�o�͐�
	 * @param targetFile
	 *            ���̓t�@�C����(�f�B���N�g��)
	 * @throws IOException
	 *             ���o�͗�O
	 */
	private static void createZip(ZipOutputStream out, File targetFile)
			throws IOException {

		if (targetFile.isDirectory()) {
			File[] files = targetFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				createZip(out, files[i]);
			}
		} else {
			ZipEntry target = new ZipEntry(getEntryPath(targetFile));
			out.putNextEntry(target);
			byte buf[] = new byte[1024];
			int count;
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(targetFile));
			while ((count = in.read(buf, 0, 1024)) != -1) {
				out.write(buf, 0, count);
			}
			in.close();
			out.closeEntry();
		}
	}

	/**
	 * ZIP�G���g���p�X��Ԃ��B
	 * 
	 * @param file
	 *            ZIP�G���g���Ώۃt�@�C��
	 * @return ZIP�G���g���̃p�X
	 */
	private static String getEntryPath(File file) {
//		int deleteLength = file.getPath().length() - file.getName().length();
//		return file.getPath().replaceAll("\\\\", "/").substring(deleteLength);
		return file.getPath().replaceAll("\\\\", "/");
	}

}
