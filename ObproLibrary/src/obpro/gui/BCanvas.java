package obpro.gui;

import java.awt.Color;
import java.awt.Font;

/**
 * �L�����o�X��\������N���X
 * 
 * �e�폑�����݃��\�b�h�ɂ��CGUI�`����s�Ȃ����Ƃ��ł��܂��D
 * ���ۂ̏������ݏ�����CanvasPanel�ɈϏ����܂��D
 * �i�]�v�ȏ������J�v�Z�������Ă��܂��̂ŁC���g��m�肽���l��CanvasPanel(BWindow.java��)���Q�Ƃ���j
 * 
 * @author macchan
 * @version 2.0
 */
public class BCanvas {

	private CanvasPanel canvasPanel;
	private CanvasKeyEventHandler keyHandler;
	private CanvasMouseEventHandler mouseHandler;

	/**
	 * �R���X�g���N�^
	 */
	public BCanvas(CanvasPanel canvasPanel, CanvasKeyEventHandler keyHandler,
			CanvasMouseEventHandler mouseHandler) {
		this.canvasPanel = canvasPanel;
		this.keyHandler = keyHandler;
		this.mouseHandler = mouseHandler;
	}

	/***************************************************
	 * �`��֘A�i��7,8��j
	 ****************************************************/

	/**
	 * ���������܂�
	 * �g�p��:
	 * ���W(10, 10) ���� ���W(20, 20)�֍������������ꍇ
	 * drawLine(Color.BLACK, 10, 10, 20, 20); 
	 */
	public void drawLine(Color color, double x1, double y1, double x2, double y2) {
		canvasPanel.drawLine(color, x1, y1, x2, y2);
	}

	/**
	 * �h��Ԃ����O�p�`�������܂�
	 * �g�p��:
	 * ���WA(10, 10), ���WB(20, 20), ���WC(10,20)�𒸓_�Ƃ���O�p�`�������ꍇ
	 * drawFillTriangle(Color.BLACK, 10, 10, 20, 20, 10, 20); 
	 */
	public void drawFillTriangle(Color color, double x1, double y1, double x2,
			double y2, double x3, double y3) {
		canvasPanel.drawFillTriangle(color, x1, y1, x2, y2, x3, y3);
	}

	/**
	 * �~�ʂ������܂�
	 * �p�x�̒P�ʂ͓x�ł��i0�`360�x)
	 * startAngle�ɂ͌ʂ�`���n�߂�p�x
	 *         90
	 * 180           0
	 *        270
	 * arcAngle�ɂ́C�ʑS�̂̊p�x�������܂��D�ʂ͔����v���ɏ�����܂�
	 * �g�p��:
	 * ���W(10, 10)������Ƃ��āC����100, ��100 �̉~�ʂ������ꍇ
	 * drawArc(Color.BLACK, 10, 10, 100, 100, 0, 360); 
	 */
	public void drawArc(Color color, double x, double y, double width,
			double height, double startAngle, double arcAngle) {
		canvasPanel.drawArc(color, x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * �h��Ԃ����~�������܂�
	 * startAngle�ɂ͌ʂ�`���n�߂�p�x
	 *         90
	 * 180           0
	 *        270
	 * arcAngle�ɂ́C�ʑS�̂̊p�x�������܂��D�ʂ͔����v���ɏ�����܂�
	 * �g�p��:
	 * ���W(10, 10)������Ƃ��āC����100, ��100 �������̉~�������ꍇ
	 * drawFillArc(Color.BLACK, 10, 10, 100, 100, 90, 180); 
	 */
	public void drawFillArc(Color color, double x, double y, double width,
			double height, double startAngle, double arcAngle) {
		canvasPanel.drawFillArc(color, x, y, width, height, startAngle,
				arcAngle);
	}

	/***************************************************
	 * �`��֘A�i��9��ȍ~�j
	 ****************************************************/

	/**
	 * �����������܂�
	 */
	public void drawText(Color color, String text, double x, double y) {
		canvasPanel.drawText(color, text, x, y);
	}

	/**
	 * �i�t�H���g�T�C�Y���w�肵�āj�����������܂�
	 */
	public void drawText(Color color, String text, double x, double y, Font font) {
		canvasPanel.drawText(color, text, x, y, font);
	}

	/**
	 * �摜�������܂�
	 */
	public void drawImage(String filename, double x, double y) {
		canvasPanel.drawImage(filename, x, y);
	}

	/**
	 * �摜�������܂�
	 * �i���ƍ����������ɂƂ�C���̑傫���Ɋg��C�k�����܂��j
	 */
	public void drawImage(String filename, double x, double y, double width,
			double height) {
		canvasPanel.drawImage(filename, x, y, width, height);
	}

	/***************************************************
	 * �t�H���g�����T�C�Y�̎擾
	 ****************************************************/

	/**
	 * �e�L�X�g�̕����擾���܂�
	 */
	public int getTextWidth(String text, Font font) {
		return canvasPanel.getTextWidth(text, font);
	}

	/**
	 * �e�L�X�g�̍������擾���܂�
	 */
	public int getTextHeight(String text, Font font) {
		return canvasPanel.getTextHeight(text, font);
	}

	/***************************************************
	 * �摜�T�C�Y�̎擾
	 ****************************************************/

	/**
	 * �摜�̕����擾���܂�
	 */
	public int getImageWidth(String filename) {
		return canvasPanel.getImageWidth(filename);
	}

	/**
	 * �摜�̍������擾���܂�
	 */
	public int getImageHeight(String filename) {
		return canvasPanel.getImageHeight(filename);
	}

	/***************************************************
	 * �X�V�֘A
	 ****************************************************/

	/**
	 * �L�����o�X�S�̂𔒂��h��Ԃ��܂�
	 */
	public void clear() {
		canvasPanel.clear();
	}

	/**
	 * �L�����o�X���X�V�i�ĕ`��j���܂�
	 */
	public void update() {
		canvasPanel.update();
		keyHandler.update();
		mouseHandler.update();
	}

	/***************************************************
	 * �L�[���͊֘A
	 ****************************************************/

	/**
	 * �����ꂽ�L�[�̃R�[�h���擾���܂�
	 */
	public int getKeyCode() {
		return keyHandler.key();
	}

	/**
	 * ���炩�̃L�[�������ꂽ���ǂ����𒲂ׂ܂��i�p���͊܂܂Ȃ��j
	 */
	public boolean isKeyDown() {
		return keyHandler.isKeyDown();
	}

	/**
	 * �w�肳�ꂽ�L�[��������Ă����Ԃ��ǂ����𒲂ׂ܂��i�p�����܂ށj
	 */
	public boolean isKeyPressing(int keycode) {
		return keyHandler.isKeyPressing(keycode);
	}

	/***************************************************
	 * �}�E�X���͊֘A
	 ****************************************************/

	/**
	 * �}�E�X��X���W���擾���܂�
	 */
	public int getMouseX() {
		return mouseHandler.mouseX();
	}

	/**
	 * �}�E�X��Y���W���擾���܂�
	 */
	public int getMouseY() {
		return mouseHandler.mouseY();
	}

	/**
	 * �}�E�X��������Ă��邩�ǂ������ׂ܂�
	 */
	public boolean isMouseDown() {
		return mouseHandler.isMouseDown();
	}

	/**
	 * �E�̃}�E�X�{�^����������Ă��邩�ǂ������ׂ܂�
	 */
	public boolean isRightMouseDown() {
		return mouseHandler.isRightMouseDown();
	}

	/**
	 * ���̃}�E�X�{�^����������Ă��邩�ǂ������ׂ܂�
	 */
	public boolean isLeftMouseDown() {
		return mouseHandler.isLeftMouseDown();
	}

	/**
	 * �N���b�N���ǂ������ׂ܂�
	 * (����ł̃N���b�N���������܂��j
	 */
	public boolean isClick() {
		return mouseHandler.isClick();
	}

	/**
	 * �V���O���N���b�N���ǂ������ׂ܂�
	 */
	public boolean isSingleClick() {
		return mouseHandler.isSingleClick();
	}

	/**
	 * �_�u���N���b�N���ǂ������ׂ܂�
	 */
	public boolean isDoubleClick() {
		return mouseHandler.isDoubleClick();
	}

	/**
	 * �h���b�O�����ǂ������ׂ܂�
	 */
	public boolean isDragging() {
		return mouseHandler.isDragging();
	}

	/***************************************************
	 * ���̑�
	 ****************************************************/

	/**
	 * �w�肳�ꂽ�b���҂��܂�
	 */
	public void sleep(double seconds) {
		try {
			Thread.sleep((long) (seconds * 1000));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �L�����p�X�̕����擾���܂�
	 */
	public int getCanvasWidth() {
		return canvasPanel.getWidth();
	}

	/**
	 * �L�����p�X�̍������擾���܂�
	 */
	public int getCanvasHeight() {
		return canvasPanel.getHeight();
	}
}