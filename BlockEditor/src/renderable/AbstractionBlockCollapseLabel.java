package renderable;

import java.awt.Color;

import codeblocks.Block;
import codeblocks.BlockConnectorShape;

/**
 * created by sakai lab 2011/10/29
 * 
 * @author yasui
 * 
 *         AbstractCollapseLabel is a label that can be added to a renderable
 *         block that will cause all blocks after this block to be hidden from
 *         view when the isCollapsed parameter is true.
 * 
 *         2012.10.06 update()�֐���Figure�ύX�̏���������ƁC�璷�Ńp�t�H�[�}���X�ɉe������̂�
 *         �i�I���W�i���ł͂����������Ă��邯�ǁj�DupdateCollapse()�̎���������������悤�ɂ���D
 */
class AbstractionBlockCollapseLabel extends CollapseLabel {

	private static final long serialVersionUID = 1L;

	AbstractionBlockCollapseLabel(long blockID) {
		super(blockID);
		this.setForeground(new Color(0, 0, 0));
		updateCollapse(true);
	}

	/**
	 * setup current visual state of button
	 */
	public void update() {
		//refreshFigure();//original
	}

	public void updateCollapse() {
		updateCollapse(false);
	}

	private void updateCollapse(boolean init) {
		originalUpdateCollapse();
		refreshFigure();
		if (!init) {
			reformRelatedBlocks();
		}
	}

	// ���ۉ��u���b�N���J���ꂽ�Ƃ��֌W����u���b�N�����t�H�[������
	// 2012.09.25 ���[�v���Ă����̂łЂƂ܂������i�Ȃ��Ă��Ȃ�Ƃ��Ȃ肻���j #matsuzawa
	// 2012.10.05 ���ꂪ�Ȃ��ƒ��ۉ��u���b�N��߂��Ƃ��ɐe�̑傫�����ς��Ȃ��D
	// 2013.09.27 ���ۉ��u���b�N��2���񂾍ۂɁA2�ڂ̒��ۉ��u���b�N��close��Ԃł���ꍇ��NullPointerException����

	private void reformRelatedBlocks() {
		try {
			Block topBlock = getTopBlock();
			if (topBlock == null) {
				return;
			}
			RenderableBlock topFigure = RenderableBlock
					.getRenderableBlock(topBlock.getBlockID());
			//RenderableBlock.getRenderableBlock()
			topFigure.redrawFromTop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Block getTopBlock() {
		Block topBlock = Block.getBlock(getBlockID());
		while (true) {
			Block beforeBlock = Block.getBlock(topBlock.getBeforeBlockID());
			if (beforeBlock == null) {
				break;
			}
			topBlock = beforeBlock;
		}

		if (topBlock == null) {
			throw new RuntimeException("ari e nai!");
		}
		if (topBlock.getBlockID().equals(getBlockID())) {
			return null;
		}
		return topBlock;
	}

	//	// ���ۉ��u���b�N���J���ꂽ�Ƃ��֌W����u���b�N�����t�H�[������ (�ۈ�o�[�W����)
	//	private void reformRelatedBlocks() {
	//		RenderableBlock beforeRBlock = RenderableBlock
	//				.getRenderableBlock(getBlockID());
	//		while (true) {
	//			long blockId = Block.getBlock(beforeRBlock.getBlockID())
	//					.getBeforeBlockID();
	//			if (blockId == Block.NULL) {
	//				break;
	//			}
	//			Block beforeBlock = Block.getBlock(blockId);
	//			reformBlock(beforeBlock);
	//			beforeRBlock = RenderableBlock.getRenderableBlock(blockId);
	//		}
	//	}
	//
	//	// ���ۉ��u���b�N���J���ꂽ�Ƃ��֌W����u���b�N�����t�H�[������ (�ۈ�o�[�W����)
	//	private void reformBlock(Block block) {
	//		if (block.isCommandBlock() || block.isAbstractionBlock()) {
	//			RenderableBlock rb = RenderableBlock
	//					.getRenderableBlock(getBlockID());
	//			rb.repaintBlock();
	//		}
	//	}

	//sakailab. original code was update()
	private void refreshFigure() {
		RenderableBlock rb = RenderableBlock.getRenderableBlock(getBlockID());

		if (rb != null) {
			int x = 0;
			int y = 0;

			y += 10 + (isActive() ? BlockConnectorShape.CONTROL_PLUG_HEIGHT : 0);
			x += 12;
			x = rb.rescale(x);
			y = rb.rescale(y);

			if (isActive()) {
				y -= 4;
			}

			setLocation(x, y);
			setSize(rb.rescale(14), rb.rescale(14));

			if (isActive()) {
				setText("+");
			} else {
				setText("-");
			}
		}
	}

	/**
	 * Sets visibility of connectorBlocks
	 */
	private void originalUpdateCollapse() {
		RenderableBlock rb = RenderableBlock.getRenderableBlock(getBlockID());
		if (rb != null) {
			//collapseAfterBlocks(rb.getBlockID());//original
			collapseInsideBlocks();//sakailab
			rb.repaintBlock();
			if (rb.getHighlightHandler() != null) {
				rb.getHighlightHandler().updateImage();
				if (rb.getHighlightHandler().getParent() != null
						&& rb.getHighlightHandler().getParent().getParent() != null)
					// force redraw to erase highlight
					rb.getHighlightHandler().getParent().getParent().repaint();
			}
		}
	}

	// sakailab 2012.10.06
	private void collapseInsideBlocks() {
		Block block = Block.getBlock(getBlockID());
		Long insideFirstBlockId = block.getSocketAt(0).getBlockID();
		if (insideFirstBlockId != Block.NULL) {
			collapseBlock(insideFirstBlockId);
			collapseAfterBlocks(insideFirstBlockId);
		}
	}
}
