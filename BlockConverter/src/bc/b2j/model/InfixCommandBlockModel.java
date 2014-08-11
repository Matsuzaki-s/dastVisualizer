package bc.b2j.model;

import java.io.PrintStream;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class InfixCommandBlockModel extends CommandBlockModel {

	@Override
	public void setName(String name) {
		super.setName(name);
		if (name.startsWith("lessthanorequalto")) {// <=���ԏd�v
			setLabel("<=");
		} else if (name.startsWith("lessthan")) {// <=���ԏd�v
			setLabel("<");
		} else if (name.startsWith("greaterthanorequalto")) {
			setLabel(">=");
		} else if (name.startsWith("greaterthan")) {
			setLabel(">");
		} else if (name.startsWith("equals-")) {
			setLabel("==");
		} else if (name.startsWith("not-equals-")) {
			setLabel("!=");
		} else if ("and".equals(name)) {
			setLabel("&&");
		} else if ("or".equals(name)) {
			setLabel("||");
		} else if (name.startsWith("sum")) {
			setLabel("+");
		} else if (name.startsWith("difference")) {
			setLabel("-");
		} else if (name.startsWith("product")) {
			setLabel("*");
		} else if (name.startsWith("quotient")) {
			setLabel("/");
		} else if (name.startsWith("remainder")) {
			setLabel("%");
		} else if ("string-append".equals(name)) {
			setLabel("+");
		} else {
			throw new RuntimeException("not supported infix expression:" + name);

		}
	}

	@Override
	public void checkError() {

		// �����̃I�y�����h
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("�u���b�N�����S�ɑg�܂�Ă��܂���C�F " + getGenusName());
		}
		BlockModel leftOperand = BlockToJavaAnalyzer.getBlock(getConnectorIDs()
				.get(0));
		leftOperand.checkError();

		// �E���̃I�y�����h
		if (getConnectorIDs().get(1) == BlockModel.NULL) {
			throw new RuntimeException("�u���b�N�����S�ɑg�܂�Ă��܂���D�F " + getGenusName());
		}
		BlockModel rightOperand = BlockToJavaAnalyzer
				.getBlock(getConnectorIDs().get(1));
		rightOperand.checkError();
	}

	@Override
	public void print(PrintStream out, int indent) {
		BlockModel leftOperand = BlockToJavaAnalyzer.getBlock(getConnectorIDs()
				.get(0));
		BlockModel rightOperand = BlockToJavaAnalyzer
				.getBlock(getConnectorIDs().get(1));

		if (getGenusName().equals("equals-string")) {
			leftOperand.print(out, indent);
			out.print(".equals");
			out.print("(");
			rightOperand.print(out, indent);
			out.print(")");
			return;
		}

		if ("string-append".equals(getGenusName())) {
			out.print("(");
			leftOperand.print(out, indent);
			out.print(" + ");
			rightOperand.print(out, indent);
			out.print(")");
			return;
		}

		// �����̃I�y�����h
		if (leftOperand instanceof InfixCommandBlockModel) {
			out.print("(");
		}
		leftOperand.print(out, indent);
		if (leftOperand instanceof InfixCommandBlockModel) {
			out.print(")");
		}

		out.print(" " + getLabel() + " ");

		// �E���̃I�y�����h
		if (rightOperand instanceof InfixCommandBlockModel) {
			out.print("(");
		}
		rightOperand.print(out, indent);
		if (rightOperand instanceof InfixCommandBlockModel) {
			out.print(")");
		}
	}
}
