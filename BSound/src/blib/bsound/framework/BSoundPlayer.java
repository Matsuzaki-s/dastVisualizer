/**
 * SoundPlayer.java
 * Created on 2006/05/26
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.framework;

/**
 * Class SoundPlayer.
 * 
 * @author macchan
 */
public abstract class BSoundPlayer {

	public enum State {
		PLAYING, PAUSING, PAUSED, STOPPING, STOPPED
	}

	private String name = "";
	private State state = State.STOPPED;
	private boolean loop = false;

	/**
	 * ����������Ȃ��ꍇ�́C��O���o������
	 */
	public BSoundPlayer(String name) {
		this.name = name;
	}

	/**
	 * ���O���擾����
	 */
	public String getName() {
		return name;
	}

	/**
	 * �Đ�����
	 */
	public abstract void play();

	/**
	 * ���t���ꎞ��~����
	 */
	public abstract void pause();

	/**
	 * ���t���~�߂�
	 */
	public abstract void stop();

	/**
	 * ���[�v���邩�ǂ������ׂ�
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * ���[�v���邩�ǂ����ݒ肷��
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * ��Ԃ𒲂ׂ�
	 */
	public State getState() {
		return state;
	}

	/**
	 * ��Ԃ�ݒ肷��
	 */
	public void setState(State state) {
		if (this.state != state) {
			this.state = state;
		}
	}

	/**
	 * �{�����[�����擾����
	 */
	public abstract int getVolume();

	/**
	 * �{�����[����ݒ肷��
	 */
	public abstract void setVolume(int volume);

	/**
	 * �����{�����[����ݒ肷��
	 */
	public abstract int getDefaultVolume();

	/**
	 * �G���[���o�͂���
	 */
	public void showError(String message) {
		System.err.println(message + " :" + getName());
	}
}
