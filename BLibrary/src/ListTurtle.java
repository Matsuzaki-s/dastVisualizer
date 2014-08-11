import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;

/*
 * ListTurtle.java
 * Created on 2012/01/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 * 
 * ���������P�Fautohide�̃f�t�H���g�l���ǂ�����ׂ���
 * 			 false(List�ւ̈ڍs�ɗL��)
 * 			 true(���ꂽ��Ȃ��Ȃ�D�D����I�Ɏ኱�L��)
 * ���������Q�Findex��0����n�߂邩�iJava�ւ̈ڍs�ɗL���j
 * 			  ����Ƃ�1����n�߂邩�i����I�ɗL���j
 */
public class ListTurtle<T extends Turtle> extends ImageTurtle {

	private static final int MARGIN = 5;

	private static final int FONTSIZE = 12;

	private LinkedList<T> children = new LinkedList<T>();
	private int cursor = 0;
	private String name;
	private Color bgColor;

	private boolean autohide = false;
	private boolean autoupdate = false;

	public ListTurtle() {
		this(false, null, null);
	}

	public ListTurtle(boolean autohide) {
		this(autohide, null, null);
	}

	public ListTurtle(boolean autohide, String name) {
		this(autohide, name, null);
	}

	public ListTurtle(boolean autohide, String name, Color bgColor) {
		this(false, autohide, name, bgColor);
	}
	
	public ListTurtle(boolean autoupdate, boolean autohide, String name, Color bgColor) {
		this.autoupdate = autoupdate;
		this.autohide = autohide;
		this.name = name;
		this.bgColor = bgColor;
		resetImage();
	}

	/***************************************************************************
	 * Cursor��
	 **************************************************************************/

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int newCursor) {
		int size = children.size();
		if (0 <= newCursor && newCursor < size) {// ����
			this.cursor = newCursor;
		} else if (size <= 0) {// �z��empty�̏ꍇ
			this.cursor = 0;
		} else if (newCursor >= 0) {// �͈͊O�i�v���X�j
			this.cursor = newCursor % size;
		} else if (newCursor < 0) {// �͈͊O�i�}�C�i�X�j
			this.cursor = size + (newCursor % size);
		} else {// ���肦�Ȃ��͂�
			throw new RuntimeException();
		}

		if (size > 0 && !(0 <= this.cursor && this.cursor < size)) {// ����
			throw new RuntimeException(
					"this.cursor is not in range. this.cursor = " + this.cursor);
		}
		resetImage();
	}

	public void moveCursorToNext() {
		setCursor(cursor + 1);
	}

	public void moveCursorToPrevious() {
		setCursor(cursor - 1);
	}

	public T getObjectAtCursor() {
		if (children.size() <= 0) {
			// 2012/01/17��w���ł͗]�v�Y�ނ̂Ŏd�l�ύX
			// return (T) NullTurtle.NULL_TURTLE;
			// 2012/01/17��O�𔭐�����悤�Ɏd�l�ύX
			if (name == null) {
				throw new RuntimeException(
						"�J�[�\����ɂȂɂ��Ȃ��̂ɁCgetObjectAtCursor()���Ă΂�܂���");
			} else {
				throw new RuntimeException(
						"�J�[�\����ɂȂɂ��Ȃ��̂ɁCgetObjectAtCursor()���Ă΂�܂����F" + name);
			}
		}
		return children.get(cursor);
	}

	public T get(int i) {
		return children.get(i);
	}

	@Deprecated
	public int getNumberAtCursor() {
		try {
			return Integer.parseInt(getStringAtCursor());
		} catch (Exception ex) {
			return -1;
		}
	}

	@Deprecated
	public String getStringAtCursor() {
		try {
			T object = children.get(cursor);
			if (object instanceof CardTurtle) {
				return ((CardTurtle) object).text();
			} else if (object instanceof TextTurtle) {
				return ((TextTurtle) object).text();
			} else {
				return object.toString();
			}
		} catch (Exception ex) {
			return "NULL";
		}
	}

	/***************************************************************************
	 * �ǉ��ƍ폜
	 **************************************************************************/

	public void add(T turtle) {
		addLast(turtle);
	}

	public void add(int index, T turtle) {
		parentCheck(turtle);
		if (index < getSize()) {
			children.add(index, turtle);
		} else {
			children.add(turtle);
		}
		doPostAddProcess(turtle);
	}

	public void addLast(T turtle) {
		parentCheck(turtle);
		children.addLast(turtle);
		doPostAddProcess(turtle);
	}

	public void addFirst(T turtle) {
		parentCheck(turtle);
		children.addFirst(turtle);
		doPostAddProcess(turtle);
	}

	public void addToCursor(T turtle) {
		addToBeforeCursor(turtle);
	}

	public void addToBeforeCursor(T turtle) {
		add(this.cursor, turtle);
	}

	public void addToAfterCursor(T turtle) {
		add(this.cursor + 1, turtle);
	}

	private void doPostAddProcess(T turtle) {
		if (autohide) {
			turtle.hide();
		}
		turtle.parent = this;
		resetImage();
	}

	private void parentCheck(T turtle) {
		if (turtle.parent != null) {
			turtle.parent.remove(turtle);
		}
	}

	public void addAll(ListTurtle from) {
		from.moveAllTo(this);
	}

	public void moveAllTo(ListTurtle to) {
		int objectCount = this.getSize();
		for (int i = 0; i < objectCount; i++) {
			to.addLast(this.getObjectAtCursor());
		}
	}

	public void removeAll() {
		for (T child : children) {
			child.die();
		}
		children.clear();
		resetImage();
	}

	public T removeAtCursor() {
		return remove(getObjectAtCursor());
	}

	public T remove(T turtle) {
		return removeObjectInternal(turtle);
	}

	public T removeFirst() {
		return removeObjectInternal(children.getFirst());
	}

	public T removeLast() {
		return removeObjectInternal(children.getLast());
	}

	public T remove(int i) {
		return removeObjectInternal(children.get(i));
	}

	private T removeObjectInternal(T turtle) {
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

	public int getSize() {
		return children.size();
	}

	public void shuffle() {
		Collections.shuffle(children);
		resetImage();
	}

	/**
	 * @return the bgColor
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @param bgColor
	 *            the bgColor to set
	 */
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
		resetImage();
	}

	
	public void setAutohide(boolean autohide) {
		this.autohide = autohide;
	}
	
	public void setAutoupdate(boolean autoupdate) {
		this.autoupdate = autoupdate;
	}

	/***************************************************************************
	 * �f�o�b�O�p
	 * 
	 * @deprecated
	 **************************************************************************/
	void printChildren() {
		int objectCount = this.getSize();
		for (int i = 0; i < objectCount; i++) {
			setCursor(i + 1);
			System.out.print(this.getNumberAtCursor() + ",");
		}
		setCursor(1);
	}

	/***************************************************************************
	 * �`��Strategy
	 **************************************************************************/

	private synchronized void resetImage() {

		// save original location
		double orgX = x() - (width() / 2d);
		double orgY = y() - (height() / 2d);
		// System.out.println(this + "(" + orgX + "," + orgY + ")");

		// calculate size
		int width;
		int height;
		if (children.size() > 0) {
			width = MARGIN;
			height = 0;
			for (T child : children) {
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

		// �h��
		if (bgColor != null) {
			g.setColor(bgColor);
			g.fillRect(0, 0, width - 1, height - 1);
		}

		// �g��
		g.setColor(color());
		g.drawRect(0, 0, width - 1, height - 1);

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
			T child = children.get(i);
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
		warp(orgX + (width() / 2d), orgY + (height() / 2d));
		
		if(autoupdate){
			update();
		}
	}
}