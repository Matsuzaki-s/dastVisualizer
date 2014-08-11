package blib.bsound;

import java.io.File;
import java.net.URL;

import blib.bsound.framework.BSoundPlayer;

/*
 * ���S�җp�@���o���N���X�D�@
 * �I�u�v�����C�҂̂��߂ɁD mp3, wav, mid�t�@�C�����ȒP�ɐ���ł��܂��D
 * 
 * �P�D��{�I�Ȏg���� BSound sound = new BSound("sample.wav"); 
 * sound.loop();
 * 
 * �Q�D���������C���X�^���X�𐶐����Ȃ��ȈՃ��\�b�h���g���ꍇ
 * BSound.play("sample.wav");
 * 
 * �P�D�͂a�f�l�C�Q�D�͌��ʉ��ɍœK�ł��D �T���v���R�[�hBSoundTest���Q�Ƃ��������D
 * 
 * ���̃N���X�ŉ����Đ������ꍇ�f�t�H���g�ŃX�g���[�~���O�Đ����s���܂����C �������x���d�v�ȏꍇ�́C�������Ƀ��[�h���Ă����K�v������܂��D
 * BSound.load("sample.wav"); ���R�Ȃ���CBGM���̒����t�@�C���́C���[�h����ƃ��������������܂��D�C�����Ă��������D
 * 
 * �Ȃ��C���݂̃o�[�W�����ł́Cmidi�t�@�C���̉��ʒ��߂͂ł��܂���D
 * 
 * version 2 2012.09.13 jdk1.7��reset�ł��Ȃ��G���[���o����CBufferedInputStream�����b�v���邱�Ƃŉ����iBWavSoundStream.java�j
 * 
 * @author macchan
 */
public class BSound {

	/*********************************************
	 * �N���X���\�b�h
	 *********************************************/

	/**
	 * �Đ�����i�~�߂��܂���j
	 */
	public static final void play(String path) {
		new BSound(path).play();
	}

	/**
	 * �{�����[�����w�肵�čĐ�����i�~�߂��܂���j
	 */
	public static final void play(String path, int volume) {
		BSound sound = new BSound(path);
		sound.setVolume(volume);
		sound.play();
	}

	/**
	 * ��������ɃT�E���h�f�[�^��ǂݍ��݂܂�(�����������Ȃ�܂����C�������̈悪�K�v�ł�)
	 */
	public static final void load(String path) {
		BSoundSystem.load(path);
	}

	/**
	 * ��������ɃT�E���h�f�[�^��ǂݍ��݂܂�(�����������Ȃ�܂����C�������̈悪�K�v�ł�)
	 */
	public static final void load(File file) {
		BSoundSystem.load(file);
	}

	/**
	 * ��������ɃT�E���h�f�[�^��ǂݍ��݂܂�(�����������Ȃ�܂����C�������̈悪�K�v�ł�)
	 */
	public static final void load(URL url) {
		BSoundSystem.load(url);
	}

	/*********************************************
	 * BSound�{��
	 *********************************************/

	private BSoundPlayer player = null;

	public BSound(String path) {
		player = BSoundSystem.createPlayer(path);
	}

	public BSound(File file) {
		player = BSoundSystem.createPlayer(file);
	}

	public BSound(URL url) {
		player = BSoundSystem.createPlayer(url);
	}

	/*
	 * ------------------------- ����n -------------------------
	 */

	/**
	 * �Đ����܂�
	 */
	public void play() {
		player.setLoop(false);
		player.play();
	}

	/**
	 * ���[�v�Đ����܂�
	 */
	public void loop() {
		player.setLoop(true);
		player.play();
	}

	/**
	 * ��~���܂�
	 */
	public void stop() {
		player.stop();
	}

	/**
	 * �Đ������ǂ������ׂ܂�
	 */
	public boolean isPlaying() {
		return player.getState() == BSoundPlayer.State.PLAYING;
	}

	/*
	 * ------------------------- �{�����[���R���g���[���n �i�{�����[����0-100��100�i�K�ݒ肪�ł��܂��j
	 * -------------------------
	 */

	/**
	 * ���݂̃{�����[�����擾���܂��D
	 */
	public int getVolume() {
		return player.getVolume();
	}

	/**
	 * �{�����[����ݒ肵�܂��D
	 */
	public void setVolume(int volume) {
		player.setVolume(volume);
	}

	/**
	 * �����{�����[�����擾���܂�
	 */
	public int getDefaultVolume() {
		return player.getDefaultVolume();
	}

}
