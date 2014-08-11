import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;

/**
 * ���ʌ݊����̂��߂ɂ���HolderTurtle�ł��D
 */
@Deprecated
public class HolderTurtle extends ImageTurtle {

	private static final int MARGIN = 5;

	private static final int FONTSIZE = 12;
	private String name;

	private LinkedList<ImageTurtle> children = new LinkedList<ImageTurtle>();
	private int cursor = 0;

	public HolderTurtle() {
		this(null);
	}

	public HolderTurtle(String name) {
		this.name = name;
		resetImage();
	}

	/***************************************************************************
	 * Cursor��
	 **************************************************************************/

	public int �J�[�\���ʒu() {
		return cursor + 1;
	}

	public void �J�[�\���ʒu��ς���(int newCursor) {
		if (newCursor >= 1 && newCursor <= children.size()) {// ����
			this.cursor = newCursor - 1;
		} else if (children.size() != 0) {// �͈͊O
			this.cursor = (newCursor % children.size()) - 1;
		} else {// 0�̏ꍇ
			this.cursor = 0;
		}
		resetImage();
	}

	public void �J�[�\����i�߂�() {
		�J�[�\���ʒu��ς���(cursor + 2);
	}

	public void �J�[�\����߂�() {
		�J�[�\���ʒu��ς���(cursor);
	}

	public ImageTurtle �J�[�\���ʒu�ɂ������() {
		if (children.size() <= 0) {
			return NullTurtle.NULL_TURTLE;
		}
		return children.get(cursor);
	}

	public ImageTurtle �ȉ��̃J�[�\���ʒu�ɂ������(int i) {
		return children.get(i - 1);
	}

	public int �J�[�\���ʒu�ɂ�����̂̐��l() {
		try {
			return Integer.parseInt(�J�[�\���ʒu�ɂ�����̂̓��e());
		} catch (Exception ex) {
			return -1;
		}
	}

	public String �J�[�\���ʒu�ɂ�����̂̓��e() {
		try {
			ImageTurtle object = children.get(cursor);
			if (object instanceof CardTurtle) {
				return ((CardTurtle) object).text();
			} else {
				return ((TextTurtle) object).text();
			}
		} catch (Exception ex) {
			return "NULL";
		}
	}

	/***************************************************************************
	 * �ǉ��ƍ폜
	 **************************************************************************/

	public void �Ō�ɒǉ�����(ImageTurtle turtle) {
		parentCheck(turtle);
		children.addLast(turtle);
		doPostAddProcess(turtle);
	}

	public void �擪�ɒǉ�����(ImageTurtle turtle) {
		parentCheck(turtle);
		children.addFirst(turtle);
		doPostAddProcess(turtle);
	}

	public void �J�[�\���ʒu�ɒǉ�����(ImageTurtle turtle) {
		parentCheck(turtle);
		children.add(this.cursor, turtle);
		doPostAddProcess(turtle);
	}

	private void doPostAddProcess(ImageTurtle turtle) {
		turtle.hide();
		turtle.parentHolder = this;
		resetImage();
	}

	private void parentCheck(ImageTurtle turtle) {
		if (turtle.parent != null) {
			turtle.parentHolder.�폜����(turtle);
		}
	}

	public void �����Ă���S�Ă̂��̂��ȉ��̓��ꕨ�Ɉړ�����(HolderTurtle to) {
		int objectCount = this.�����Ă�����̂̌�();
		for (int i = 0; i < objectCount; i++) {
			to.�Ō�ɒǉ�����(this.�J�[�\���ʒu�ɂ������());
		}
	}

	public void �����Ă�����̂�S�Ď̂Ă�() {
		for (ImageTurtle child : children) {
			child.die();
		}
		children.clear();
		resetImage();
	}

	public void �J�[�\���ʒu�̂��̂��폜����() {
		�폜����(�J�[�\���ʒu�ɂ������());
	}

	public void �폜����(ImageTurtle turtle) {
		removeObjectInternal(turtle);
	}

	private ImageTurtle removeObjectInternal(ImageTurtle turtle) {
		if (turtle == null || !children.contains(turtle)) {
			System.err.println("�폜�ł��܂���");
			return null;
		}

		children.remove(turtle);
		turtle.parent = null;
		turtle.show();
		resetImage();
		return turtle;
	}

	/***************************************************************************
	 * ���̑�Public
	 **************************************************************************/

	public int �����Ă�����̂̌�() {
		return children.size();
	}

	public void �����܂���() {
		Collections.shuffle(children);
		resetImage();
	}

	/***************************************************************************
	 * �f�o�b�O�p
	 * 
	 * @deprecated
	 **************************************************************************/
	void printChildren() {
		int objectCount = this.�����Ă�����̂̌�();
		for (int i = 0; i < objectCount; i++) {
			�J�[�\���ʒu��ς���(i + 1);
			System.out.print(this.�J�[�\���ʒu�ɂ�����̂̐��l() + ",");
		}
		�J�[�\���ʒu��ς���(1);
	}

	/***************************************************************************
	 * �`��Strategy
	 **************************************************************************/

	private synchronized void resetImage() {

		// save original location
		double orgX = x() - (width() / 2d);
		double orgY = y() - (height() / 2d);
		System.out.println(this + "(" + orgX + "," + orgY + ")");

		// calculate size
		int width;
		int height;
		if (children.size() > 0) {
			width = MARGIN;
			height = 0;
			for (ImageTurtle child : children) {
				width += (int) child.image().getWidth() + MARGIN;
				height = height > (int) child.height() ? height : (int) child
						.height();
			}
			height += MARGIN * 2;
		} else {
			// Default Size
			width = 60;
			height = 30;
		}
		if (name != null) {// ���O�̕��𑫂�
			height += FONTSIZE + MARGIN;
			int nameW = (int) (name.length() * (FONTSIZE * 6d / 5d));
			if (width < nameW) {
				width = nameW;
			}
		}

		// ����
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) image.getGraphics();

		// �g��
		g.setColor(color());
		g.drawRect(1, 1, width - 2, height - 2);

		int x = MARGIN;
		int y = MARGIN;

		// ���O
		if (name != null) {
			y += FONTSIZE;
			g.drawString(name, MARGIN, y);
			y += MARGIN;
		}

		// children
		for (int i = 0; i < children.size(); i++) {
			ImageTurtle child = children.get(i);
			g.drawImage(child.image(), x, y, null);

			// �J�[�\��
			if (cursor == i) {
				g.setColor(Color.red);
				Stroke original = g.getStroke();
				g.setStroke(new BasicStroke(3));
				g.drawRect(x, y, child.image().getWidth(), child.image()
						.getHeight());
				g.setStroke(original);
				g.setColor(color());
			}

			x += child.image().getWidth() + MARGIN;
		}

		g.dispose();

		// set new image
		super.setImage(image);

		// set to original x, y
		int newX = (int) (orgX + (width / 2d) + 0.1);
		int newY = (int) (orgY + (height / 2d) + 0.1);
		warp(newX, newY);
	}
}