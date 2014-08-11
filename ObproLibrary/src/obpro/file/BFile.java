package obpro.file;
/*
 * BFile.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * �t�@�C����\������N���X�i���S�җp�j
 * 
 * @author macchan
 * @version $Id: BFile.java,v 1.1 2007/06/13 07:45:06 macchan Exp $
 */
public class BFile {

	private File file = null;

	/**
	 * �R���X�g���N�^
	 */
	public BFile(String filePath) {
		try {
			file = new File(filePath);
			if (!file.exists()) {
				makeDirectory(filePath);
				file.createNewFile();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �������݃X�g���[�����J��
	 */
	public BFileWriter openWriter() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			return new BFileWriter(fileOutputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * �ǂݍ��݃X�g���[�����J��
	 */
	public BFileReader openReader() {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			return new BFileReader(fileInputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * �K�v�ȃf�B���N�g�����쐬����
	 */
	public void makeDirectory(String filePath) {
		if (filePath.contains("/")) {
			String directoryPath = filePath.substring(0, filePath
					.lastIndexOf("/"));
			File parentDirectory = new File(directoryPath);
			parentDirectory.mkdirs();
		}
	}
}
