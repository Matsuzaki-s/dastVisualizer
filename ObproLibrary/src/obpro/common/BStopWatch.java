/*
 * BStopWatch.java
 * Created on 2007/05/24 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.common;

/**
 * BStopWatch
 */
public class BStopWatch {

	private long startTime = 0;
	private long stopTime = 0;

	/**
	 * �X�^�[�g����
	 */
	public void start() {
		startTime = System.nanoTime();
	}

	/**
	 * �X�g�b�v����
	 */
	public void stop() {
		stopTime = System.nanoTime();
	}

	/**
	 * �����������Ԃ��擾����
	 */
	public long getTimeByMiliseconds() {
		return (stopTime - startTime) / 1000000;
	}
}
