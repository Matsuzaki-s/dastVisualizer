package obpro.file;

/*
 * BFileWriter.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * �t�@�C���������݃X�g���[����\������N���X�i���S�җp�j
 * 
 * @author macchan
 * @version $Id: BFileWriter.java,v 1.1 2007/06/13 07:45:06 macchan Exp $
 */
public class BFileWriter {

	private PrintStream printStream;

	/**
	 * �R���X�g���N�^
	 */
	public BFileWriter(FileOutputStream fileOutputStream) {
		printStream = new PrintStream(fileOutputStream);
	}

	/**
	 * ���������������
	 */
	public void print(String s) {
		printStream.print(s);
	}
	
	/**
	 * ��������������
	 */
	public void print(long i) {
		printStream.print(i);
	}
	
	/**
	 * ��������������
	 */
	public void print(double d) {
		printStream.print(d);
	}
	
	/**
	 * ��������������
	 */
	public void print(char c) {
		printStream.print(c);
	}

	/**
	 * ���������������ŁA���s����
	 */
	public void println(String s) {
		print(s);
		println();
	}
	
	/**
	 * ��������������
	 */
	public void println(long i) {
		printStream.println(i);
	}
	
	/**
	 * ��������������
	 */
	public void println(double d) {
		printStream.println(d);
	}
	
	/**
	 * ��������������
	 */
	public void println(char c) {
		printStream.println(c);
	}
	
	/**
	 * ���s����
	 */
	public void println() {
		printStream.println();
	}

	/**
	 * �X�g���[�������
	 */
	public void close() {
		this.printStream.close();
	}

}