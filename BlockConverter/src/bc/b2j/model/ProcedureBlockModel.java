package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class ProcedureBlockModel extends CommandBlockModel {

	@Override
	public void checkError() {

		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(connectorID).checkError();
			}
		}

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	@Override
	public void print(PrintStream out, int indent) {
		String returnType = getReturnType();
		// makeIndent(out, indent);

		// BlockToJavaAnalyzer.getBlock(373).print(out, indent);
		// main �̐擪���C�����l�����āA������O�ɏ������ޕK�v

		out.print("public");
		if ("main".equals(getLabel())) {
			out.print(" static");
		}
		out.print(" ");
		out.print(returnType);
		out.print(" " + getLabel() + "(");

		// �����̒�`
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int i = 0; i + 1 < connectorIDs.size(); i++) {
			int connectorID = connectorIDs.get(i);
			BlockToJavaAnalyzer.getBlock(connectorID).print(out, indent);
			if (connectorIDs.get(i + 1) != BlockModel.NULL) {
				out.print(", ");
			}
		}

		out.println(") {");
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent + 1);
		}
		makeIndent(out, indent);
		if(isCollapsed()){
			out.print("}//" + getComment() + " @(" + getX() + ", " + getY() + ")" + " [close]");	
		}else{
			out.print("}//" + getComment() + " @(" + getX() + ", " + getY() + ")" + " [open]");
		}
		
	}

	public String getReturnType() {
		// Calc!! ���̃��f���ɂ͖��������邯�ǁD�D�D #matsuzawa
		for (BlockModel child : getAllChildren()) {
			if (child instanceof ReturnBlockModel) {
				return ((ReturnBlockModel) child).getReturnType();
			}
		}
		return "void";
	}

	public List<BlockModel> getAllChildren() {
		return getChildren(this);
	}

	public List<BlockModel> getChildren(BlockModel model) {
		List<BlockModel> children = new ArrayList<BlockModel>();
		if (model.getAfterID() != -1) {
			BlockModel child = BlockToJavaAnalyzer.getBlock(model.getAfterID());
			if (child != null) {
				children.add(child);
				children.addAll(getChildren(child));
			}
		}
		for (int id : model.getConnectorIDs()) {
			BlockModel child = BlockToJavaAnalyzer.getBlock(id);
			if (child != null) {
				children.add(child);
				children.addAll(getChildren(child));
			}
		}
		return children;
	}
	
}
