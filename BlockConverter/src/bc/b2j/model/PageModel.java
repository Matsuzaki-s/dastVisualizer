package bc.b2j.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import bc.BCSystem;
import bc.apps.OutputSourceModel;

public class PageModel extends BlockModel {

	private ArrayList<ProcedureBlockModel> procedures = new ArrayList<ProcedureBlockModel>();
	private ArrayList<ConstructorBlockModel> constructors = new ArrayList<ConstructorBlockModel>();
	private ArrayList<PrivateVariableBlockModel> privateVariableBlocks = new ArrayList<PrivateVariableBlockModel>();
	private String superClass = null;

	public PageModel(String name, String superClass) {
		setName(name);
		this.superClass = superClass;
	}

	/**
	 * 
	 * @param procedures
	 */
	public void addProcedure(ProcedureBlockModel procedure) {
		procedures.add(procedure);
	}

	public void addProcedure(ConstructorBlockModel procedure) {
		constructors.add(procedure);
	}
	
	public void addPrivateVariableBlock(
			PrivateVariableBlockModel privateVariableBlock) {
		privateVariableBlocks.add(privateVariableBlock);
	}
	
	public void addConstructor(ConstructorBlockModel constructorBlock){
		constructors.add(constructorBlock);
	}
	/**
	 * 
	 * @param superClass
	 */
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	/**
	 * 
	 * @return
	 */
	public String getSuperClass() {
		return superClass;
	}

	@Override
	public void checkError() {
		for (BlockModel topBlock : procedures) {
			topBlock.checkError();
		}
	}

	// TODO
	// BlockEditor����u���b�N�Ńv���O������g�ݗ��āAJava�̃\�[�X�R�[�h�ɂ���Ƃ���class����BlockEditor�̏����ݒ�̃y�[�W���ɂ��āAmain���\�b�h���L�q���Ă�����K�v������B
	@Override
	public void print(PrintStream out, int indent) {
		for (BlockModel topBlock : procedures) {
			topBlock.print(out, indent + 1);
		}
	}

	// TODO Java����BlockEditor�̃u���b�N�ɕϊ����ꂽ�Ƃ��̓I���W�i����Java�\�[�X�R�[�h�ƃ��\�b�h�̕��������u��������
	public void print2(OutputSourceModel out) {

		BCSystem.out.println("call print2 method at PageModel");
		BCSystem.out.println("procedure size:" + procedures.size());

		for (PrivateVariableBlockModel privateVariableBlock : privateVariableBlocks) {//#ohata added
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(byteArray);
			privateVariableBlock.print(ps, 0);
			String blockString = byteArray.toString();			
			String name = privateVariableBlock.getLabel();			
			out.replacePrivateValue(name,blockString);//private�ϐ��͌ʂɓo�^���Ă���
		}
		
		for (ConstructorBlockModel constructor : constructors) {// ���ׂĂ̎葱���u���b�N���v�����g����//#ohata added
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(byteArray);
			constructor.print(ps, 0);
			String blockString = byteArray.toString();
			BCSystem.out.println("blockString:" + blockString);
			String name = constructor.getLabel();
			BCSystem.out.println("name:" + name);
			out.replace(name, blockString);
		}
		
		for (ProcedureBlockModel procedure : procedures) {// ���ׂĂ̎葱���u���b�N���v�����g����
			BCSystem.out.println("procedure block model print");
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(byteArray);
			procedure.print(ps, 0);
			String blockString = byteArray.toString();
			BCSystem.out.println("blockString:" + blockString);
			String name = procedure.getLabel();
			BCSystem.out.println("name:" + name);
			out.replace(name, blockString);
		}
	}
/*	
 //#ohata added
	private String getBlockStringValue(String blockString){
		int index = blockString.indexOf("=");
		if(index == -1){
			return null;
		}
		return blockString.substring(index+2,blockString.length()-2);
	}
	*/
}


