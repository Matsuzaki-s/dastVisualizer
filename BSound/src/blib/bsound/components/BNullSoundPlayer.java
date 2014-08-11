/**
 * NullSoundPlayer.java
 * Created on 2006/05/27
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.components;

import blib.bsound.framework.BSoundPlayer;



/**
 * Class NullSoundPlayer.
 * 
 * @author macchan
 */
public class BNullSoundPlayer extends BSoundPlayer {

	/**
	 * Constructor
	 */
	public BNullSoundPlayer(String name) {
		super(name + " (load error)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.SoundPlayer#getVolume()
	 */
	public int getVolume() {
		showError("�{�����[���擾�ł��܂���");
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.SoundPlayer#pause()
	 */
	public void pause() {
		showError("pause�ł��܂���");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.SoundPlayer#play()
	 */
	public void play() {
		showError("play�ł��܂���");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.SoundPlayer#setVolume(int)
	 */
	public void setVolume(int volume) {
		showError("�{�����[���ݒ�ł��܂���");
	}

	/* (non-Javadoc)
	 * @see obpro.bsound.framework.BSoundPlayer#getDefaultVolume()
	 */
	public int getDefaultVolume() {
		showError("�{�����[���擾�ł��܂���");
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.SoundPlayer#stop()
	 */
	public void stop() {
		showError("stop�ł��܂���");
	}

}
