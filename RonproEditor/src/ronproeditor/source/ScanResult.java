/*
 * ScanResult.java
 * Created on 2007/09/22 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.source;

/**
 *�@��s�X�L�����������ʂ�\�킷�C���i�[�N���X�ł��B
 */
public class ScanResult {
	private int openBraceCount = 0;
	private int closeBraceCount = 0;
	private boolean inComment = false;
	private boolean isCloseSecond = false;

	/**
	 * �R���X�g���N�^�ł��B
	 */
	ScanResult() {
	}

	/**
	 *�@�ǂݍ��񂾍s�̊J�������ʂ̐��𑝂₵�܂�
	 */
	public void addOpenBraceCount() {
		openBraceCount++;
	}

	/**
	 *�@�ǂݍ��񂾍s�̕������ʂ̐��𑝂₵�܂�
	 */
	public void addCloseBraceCount() {
		closeBraceCount++;
		if (openBraceCount > 0) {
			isCloseSecond = true;
		}
	}

	/**
	 * ���J�b�R���J�����ʂ̂��ƂɌĂ΂ꂽ�Ƃ�
	 * �v�����  {return;}  ���̍s�������Ƃ���
	 * �P��Ԃ��܂��B
	 */
	public int getCloseSecond() {
		return isCloseSecond ? 1 : 0;
	}

	//  ----------------- setter & getter ----------------

	public void setInComment(boolean inComment) {
		ScanResult.this.inComment = inComment;
	}

	public int getOpenBraceCount() {
		return openBraceCount;
	}

	public int getCloseBraceCount() {
		return closeBraceCount;
	}

	public boolean getInComment() {
		return inComment;
	}

}
