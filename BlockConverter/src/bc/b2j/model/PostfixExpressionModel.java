package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class PostfixExpressionModel extends BlockModel {

	@Override
	public void checkError() {
		resolveCreatedVariable(getBeforeID());
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("�u���b�N�����S�ɑg�܂�Ă��܂���E�F " + getGenusName());
		}
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).checkError();
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	private void resolveCreatedVariable(int blockID) {
		if (blockID == BlockModel.NULL) {
			throw new RuntimeException("�ϐ��錾������O�ɕϐ��ւ̑�����s���Ă��܂��B");
		}
		BlockModel block = BlockToJavaAnalyzer.getBlock(blockID);
		if (block instanceof LocalVariableBlockModel) {
			return;
		}
		resolveCreatedVariable(block.getBeforeID());
	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);

		out.print(getLabel());
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				if (BlockToJavaAnalyzer.getBlock(connectorID).getLabel()
						.equals("1")) {
					out.print("++");
				} else if (BlockToJavaAnalyzer.getBlock(connectorID).getLabel()
						.equals("-1")) {
					out.print("--");
				} else if (Integer.parseInt(BlockToJavaAnalyzer.getBlock(
						connectorID).getLabel()) < -1) {
					out.print(" = ");
					out.print(getLabel()
							+ BlockToJavaAnalyzer.getBlock(connectorID)
									.getLabel());
				} else if (Integer.parseInt(BlockToJavaAnalyzer.getBlock(
						connectorID).getLabel()) > 1) {
					out.print(" = ");
					out.print(getLabel()
							+ "+"
							+ BlockToJavaAnalyzer.getBlock(connectorID)
									.getLabel());
				} else if (Integer.parseInt(BlockToJavaAnalyzer.getBlock(
						connectorID).getLabel()) == 0) {
					out.print(" = ");
					out.print(getLabel());
				}

			}
		}

		out.println(";");

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}
	}
}
