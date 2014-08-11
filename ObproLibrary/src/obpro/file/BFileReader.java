package obpro.file;

/*
 * BFileReader.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * �t�@�C���ǂݍ��݃X�g���[����\������N���X�i���S�җp�j
 * 
 * @author macchan
 * @version $Id: BFileReader.java,v 1.1 2007/06/13 07:45:06 macchan Exp $
 */
public class BFileReader {

	private String buf = null;

	private BufferedReader reader = null;

	/**
	 * �R���X�g���N�^
	 */
	public BFileReader(InputStream inputStream) {
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
	}

	/**
	 * �X�g���[�����Ō�܂œǂݍ��񂾂��ǂ������ׂ�
	 * �i���̂Ƃ��ɁA���̍s��ǂݍ���ł��܂��j
	 */
	public boolean isEndOfFile() {
		try {
			if (buf == null) {
				buf = this.reader.readLine();
			}
			return buf == null;
		} catch (IOException ex) {
			ex.printStackTrace();
			return true;
		}
	}

	/**
	 * ��s�ǂݍ���
	 */
	public String readLine() {
		if (isEndOfFile()) {
			return null;
		}
		String read = buf;
		buf = null;
		return read;
	}

	/**
	 * �X�g���[�������
	 */
	public void close() {
		try {
			this.reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}