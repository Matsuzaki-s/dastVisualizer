package bc.j2b.analyzer;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import bc.BlockConverter;
import bc.j2b.model.ClassModel;
import bc.j2b.model.CompilationUnitModel;
import bc.j2b.model.ElementModel;
import bc.j2b.model.ExCallActionMethodModel;
import bc.j2b.model.ExCallActionMethodModel2;
import bc.j2b.model.ExCallGetterMethodModel;
import bc.j2b.model.ExCallMethodModel;
import bc.j2b.model.ExCallUserMethodModel;
import bc.j2b.model.ExCastModel;
import bc.j2b.model.ExClassInstanceCreationModel;
import bc.j2b.model.ExInfixModel;
import bc.j2b.model.ExLeteralModel;
import bc.j2b.model.ExNotModel;
import bc.j2b.model.ExPostfixModel;
import bc.j2b.model.ExSpecialExpressionModel;
import bc.j2b.model.ExVariableGetterModel;
import bc.j2b.model.ExVariableSetterModel;
import bc.j2b.model.ExpressionModel;
import bc.j2b.model.StAbstractionBlockModel;
import bc.j2b.model.StBlockModel;
import bc.j2b.model.StBreakStatementModel;
import bc.j2b.model.StConstructorDeclarationModel;
import bc.j2b.model.StEmptyStatementModel;
import bc.j2b.model.StExpressionModel;
import bc.j2b.model.StIfElseModel;
import bc.j2b.model.StLocalVariableModel;
import bc.j2b.model.StMethodDeclarationModel;
import bc.j2b.model.StPrivateVariableDeclarationModel;
import bc.j2b.model.StReturnModel;
import bc.j2b.model.StVariableDeclarationModel;
import bc.j2b.model.StWhileModel;
import bc.j2b.model.StatementModel;

public class JavaToBlockAnalyzer extends ASTVisitor {

	// private static final int FIRST_ID_COUNTER = 245;
	private static final int FIRST_ID_COUNTER = 1000;

	private IdCounter idCounter = new IdCounter(FIRST_ID_COUNTER);
	private VariableResolver variableResolver = new VariableResolver();
	private MethodResolver methodResolver = new MethodResolver();

	private CompilationUnitModel currentCompilationUnit;
	private CompilationUnit compilationUnit;
	// private ClassModel currentClass;

	// ���ۉ��u���b�N�̃R�����g
	private HashMap<Integer, String> abstractionComments = new HashMap<Integer, String>();
	private JavaCommentManager commentGetter;

	/** created by sakai lab 2011/11/22 */
	// private AbstractionBlockByTagParser abstParser;

	public JavaToBlockAnalyzer(File file, String enc) {
		this.commentGetter = new JavaCommentManager(file, enc);
		// arranged by sakai lab 2011/11/22
		// abstParser = new AbstractionBlockByTagParser(file);
		// StGlobalVariableModel variable = new StGlobalVariableModel();
		// variable.setName("window");
		// variable.setType("TurtleFrame");
		// variableResolver.addGlobalVariable(variable);
	}

	public CompilationUnitModel getCompilationUnit() {
		return currentCompilationUnit;
	}

	@Override
	public boolean visit(CompilationUnit node) {
		compilationUnit = node;
		CompilationUnitModel model = new CompilationUnitModel();
		currentCompilationUnit = model;

		@SuppressWarnings("unchecked")
		List<Comment> comments = node.getCommentList();

		for (Comment comment : comments) {
			if (comment instanceof LineComment) {
				abstractionComments
						.put(comment.getStartPosition(), commentGetter
								.getLineComment(comment.getStartPosition()));
			} else if (comment instanceof BlockComment) {
				abstractionComments.put(comment.getStartPosition(),
						commentGetter.getBlockComment(comment
								.getStartPosition()));
			}
		}
		return true;
	}

	@Override
	public boolean preVisit2(ASTNode node) {
		return true;
	}

	/**
	 * Class�̉��
	 * 
	 * @param node
	 *            :TypeDeclaration�m�[�h
	 */
	@Override
	public boolean visit(TypeDeclaration node) {

		variableResolver.resetGlobalVariable();
		methodResolver.reset();
		ClassModel model = new ClassModel();
		model.setName(node.getName().toString());
		if (node.getSuperclassType() != null) {
			model.setSuperClass(node.getSuperclassType().toString());
		}
		currentCompilationUnit.addClass(model);
		// this.currentClass = model;

		// 2�p�X�K�v #matsuzawa 2012.11.24

		for (MethodDeclaration method : node.getMethods()) {
			createStub(method);
		}

		// #ohata replaced
		int x = 50;
		int y = 50;
		for (FieldDeclaration fieldValue : node.getFields()) {
			if (addPrivateVariableDeclarationModel(model, fieldValue, x, y)) {
				y += model.getPrivateValues()
						.get(model.getPrivateValues().size() - 1)
						.getBlockHeight();
			}
		}

		for (MethodDeclaration method : node.getMethods()) {
			if (x > 1000) {// �Ƃ肠����������...
				x = 50;
				y += 200;
			}

			if (method.isConstructor()) {
				if (addConstructorModel(model, method, x, y)) {
					if (model.getConstructors()
							.get(model.getConstructors().size() - 1).getPosX() == x) {
						x += 200;
					}
				}
			} else {
				if (addMethodModel(model, method, x, y)) {
					if (model.getMethods().get(model.getMethods().size() - 1)
							.getPosX() == x) {
						x += 200;
					}
				}
			}
		}
		return true;
	}

	private boolean addPrivateVariableDeclarationModel(ClassModel model,
			FieldDeclaration fieldValue, int x, int y) {

		StPrivateVariableDeclarationModel privateVariableModel = analyzePrivateValue(fieldValue);

		int index = commentGetter.getLineCommentPosition(fieldValue
				.getStartPosition() + fieldValue.getLength());

		if (privateVariableModel != null) {
			String lineComment = commentGetter.getLineComment(index);
			String position = getPositionFromLineComment(lineComment);
			// set position
			privateVariableModel.setPosX(getX(position, x));
			privateVariableModel.setPosY(getY(position, y));
			// �R�����g�̒ǉ�
			privateVariableModel.setComment(lineComment.substring(0,
					getCommentEndIndex(lineComment)));
			model.addPrivateVariable(privateVariableModel);
			return true;
		}
		return false;
	}

	// #ohata added
	private int getCommentEndIndex(String lineComment) {
		String position = getPositionFromLineComment(lineComment);
		if (position == null) {
			return lineComment.length();
		} else {
			return lineComment.indexOf(position);
		}
	}

	private boolean getOpenCloseInfoFromLineComment(String lineComment) {
		// #ohata added default:open
		// �V�����N���X������ĈڐA�\��
		if (lineComment.indexOf("[close]") != -1) {
			return true;
		} else if (lineComment.indexOf("[open]") != -1) {
			return false;
		} else {
			return false;// default open
		}
		/*
		 * int state = 0; for (int i = 0; i < lineComment.length(); i++){ switch
		 * (state) { case 0: if(lineComment.charAt(i) == '['){ state = 1; }
		 * break; case 1: if(lineComment.charAt(i) == 'c'){ state = 2; }else {
		 * state = 0; } break; case 2: if(lineComment.charAt(i) == 'l'){ state =
		 * 3; }else { state = 0; } break; case 3: if(lineComment.charAt(i) ==
		 * 'o'){ state = 4; }else { state = 0; } break; case 4:
		 * if(lineComment.charAt(i) == 's'){ state = 5; }else { state = 0; }
		 * break; case 5: if(lineComment.charAt(i) == 'e'){ state = 6; }else {
		 * state = 0; } break; case 6: if(lineComment.charAt(i) == ']'){ return
		 * true; }else{ state = 0; } break; default: break; } } return false;
		 */
	}

	private String getPositionFromLineComment(String lineComment) {
		// #ohata added
		// �V�����N���X������ĈڐA
		int state = 0;
		int start = 0;

		for (int i = 0; i < lineComment.length(); i++) {
			switch (state) {
			case 0:
				if (lineComment.charAt(i) == '@') {
					state = 1;
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 1:
				if (lineComment.charAt(i) == '(') {
					state = 2;
				} else if (lineComment.charAt(i) == ' ') {
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 2:
				if (lineComment.charAt(i) == ' ') {// �󔒕����͑f�ʂ�
				} else if (Character.isDigit(lineComment.charAt(i))) {
					state = 3;
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 3:
				if (Character.isDigit(lineComment.charAt(i))) {
				} else if (lineComment.charAt(i) == ',') {
					state = 4;
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 4:
				if (Character.isDigit(lineComment.charAt(i))) {
					state = 5;
				} else if (lineComment.charAt(i) == ' ') {
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			case 5:
				if (Character.isDigit(lineComment.charAt(i))) {
				} else if (lineComment.charAt(i) == ')') {
					int end = i + 1;
					return lineComment.substring(start, end);
				} else {
					state = 0;
					start = i + 1;
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	private boolean addConstructorModel(ClassModel model,
			MethodDeclaration method, int x, int y) {
		// #ohata added
		StConstructorDeclarationModel constructorModel = analyzeConstructor(method);

		int index = commentGetter.getLineCommentPosition(method
				.getStartPosition() + method.getLength());// ������-1���A���Ă���ƁA

		if (constructorModel != null) {
			String lineComment = commentGetter.getLineComment(index);
			String position = getPositionFromLineComment(lineComment);
			// ���W�̎w��
			constructorModel.setPosX(getX(position, x));
			constructorModel.setPosY(getY(position, y));
			// comment set
			constructorModel.setComment(lineComment.substring(0,
					getCommentEndIndex(lineComment)));// �������łւ�ȏꏊ���Ƃ����Ⴄ
			// open/close set
			constructorModel
					.setCollapsed(getOpenCloseInfoFromLineComment(lineComment));
			model.addConstructor(constructorModel);
			return true;
		}
		return false;
	}

	private boolean addMethodModel(ClassModel model, MethodDeclaration method,
			int x, int y) {
		// #ohata added
		StMethodDeclarationModel methodModel = analyzeMethod(method);

		int index = commentGetter.getLineCommentPosition(method
				.getStartPosition() + method.getLength());

		if (methodModel != null) {
			String lineComment = commentGetter.getLineComment(index);
			String position = getPositionFromLineComment(lineComment);
			// position set
			methodModel.setPosX(getX(position, x));
			methodModel.setPosY(getY(position, y));
			// comment set
			methodModel.setComment(lineComment.substring(0,
					getCommentEndIndex(lineComment)));
			// open/close set
			methodModel
					.setCollapsed(getOpenCloseInfoFromLineComment(lineComment));
			model.addMethod(methodModel);
			return true;
			// open/close
		}
		return false;
	}

	private int getX(String position, int x) {
		// #ohata added
		int posX = x;
		if (position != null) {
			return getNumber(position);
		}
		return posX;
	}

	private int getNumber(String str) {
		int start = 0;
		int state = 0;
		for (int i = 0; i <= str.length(); i++) {
			switch (state) {
			case 0:
				if (Character.isDigit(str.charAt(i))) {
					state = 1;
				} else {
					start = i + 1;
					state = 0;
					break;
				}
			case 1:
				if (Character.isDigit(str.charAt(i))) {
				} else {
					return Integer.parseInt(str.substring(start, i));
				}
				break;
			default:
				break;
			}
		}
		return -1;
	}

	private int getY(String position, int y) {
		// #ohata added
		int posY = y;
		if (position != null) {
			return getNumber(position.substring(position.indexOf(','),
					position.length()));
		}
		return posY;
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		// this.currentClass = null;
	}

	@SuppressWarnings("unchecked")
	private void createStub(MethodDeclaration method) {
		if (method.isConstructor()) {
			methodResolver.putUserConstructor(method.getName().toString(),
					(List<SingleVariableDeclaration>) method.parameters());
		} else {
			methodResolver.putUserMethod(method.getName().toString(),
					(List<SingleVariableDeclaration>) method.parameters(),
					method.getReturnType2().toString());
		}

	}

	private StPrivateVariableDeclarationModel analyzePrivateValue(
			FieldDeclaration node) {
		StPrivateVariableDeclarationModel model = new StPrivateVariableDeclarationModel();
		int index = node.fragments().get(0).toString().indexOf("=");

		model.setType(node.getType().toString());
		model.setId(idCounter.getNextId());
		model.setName(node.fragments().get(0).toString()
				.substring(0, node.fragments().get(0).toString().length()));

		// initialize���x���̓\��t��
		if (index != -1) {
			model.setName(node
					.fragments()
					.get(0)
					.toString()
					.substring(0,
							node.fragments().get(0).toString().indexOf("=")));
			VariableDeclaration val = ((VariableDeclaration) node.fragments()
					.get(0));
			model.setInitializer(parseExpression(val.getInitializer()));
		}

		variableResolver.addGlobalVariable(model);

		return model;

	}

	// /**
	// * ���\�b�h��`�̉��
	// *
	// * @param node
	// * :MethodDeclaration�m�[�h
	// * @return
	// */
	// @Override
	// public boolean visit(MethodDeclaration node) {

	private StConstructorDeclarationModel analyzeConstructor(
			MethodDeclaration node) {
		StConstructorDeclarationModel model = new StConstructorDeclarationModel();

		variableResolver.resetLocalVariable();

		// ���\�b�h�����̏���
		for (Object o : node.parameters()) {
			SingleVariableDeclaration arg = ((SingleVariableDeclaration) o);
			StLocalVariableModel argModel = createLocalVariableModel(arg
					.getType().toString(), arg.getName().toString(),
					arg.getInitializer(), true);
			model.addArgument(argModel);
		}

		// SingleVariableDeclaration
		model.setName(node.getName().toString());
		model.setId(idCounter.getNextId());

		// currentClass.addMethod(model);

		// �R���X�g���N�^�̒��g
		StBlockModel body = parseBlockStatement(node.getBody());
		body.setParent(model);
		model.setBody(body);

		// return true;
		return model;

	}

	private StMethodDeclarationModel analyzeMethod(MethodDeclaration node) {
		if ("main".equals(node.getName().toString())) {
			// return false;
			return null;
		}

		variableResolver.resetLocalVariable();

		StMethodDeclarationModel model = new StMethodDeclarationModel();
		// ���\�b�h�����̏���
		for (Object o : node.parameters()) {
			SingleVariableDeclaration arg = ((SingleVariableDeclaration) o);
			StLocalVariableModel argModel = createLocalVariableModel(arg
					.getType().toString(), arg.getName().toString(),
					arg.getInitializer(), true);
			argModel.setLineNumber(compilationUnit.getLineNumber(arg
					.getStartPosition()));
			model.addArgument(argModel);
		}
		// SingleVariableDeclaration
		model.setName(node.getName().toString());
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// currentClass.addMethod(model);

		// ���\�b�h�̒��g
		StBlockModel body = parseBlockStatement(node.getBody());
		body.setParent(model);
		model.setBody(body);

		// return true;
		return model;
	}

	/************************************************************
	 * �ȉ��CStatement�̉�͌n
	 ************************************************************/

	/**
	 * �eStatement�ւ̌o�R�֐�
	 * 
	 * @param stmt
	 *            �FStatement�m�[�h
	 * @return �eStatement�̉�͌���
	 */
	public StatementModel parseStatement(Statement stmt) {
		try {
			if (stmt instanceof Block && stmt.getParent() instanceof Block) {
				return parseAbstractionBlock((Block) stmt);
			} else if (stmt instanceof Block) {
				return parseBlockStatement((Block) stmt);
			} else if (stmt instanceof IfStatement) {
				return parseIfStatement((IfStatement) stmt);
			} else if (stmt instanceof WhileStatement) {
				return parseWhileStatement((WhileStatement) stmt);
			} else if (stmt instanceof DoStatement) {
				return parseDoStatement((DoStatement) stmt);
			} else if (stmt instanceof ForStatement) {
				return parseForStatement((ForStatement) stmt);
			} else if (stmt instanceof ExpressionStatement) {
				return parseExpressionStatement((ExpressionStatement) stmt);
			} else if (stmt instanceof VariableDeclarationStatement) {
				return parseVariableDeclarationStatement((VariableDeclarationStatement) stmt);
			} else if (stmt instanceof ReturnStatement) {
				return analyzeReturnStatement((ReturnStatement) stmt);
			} else if (stmt instanceof BreakStatement) {
				return analyzeBreakStatement((BreakStatement) stmt);
			} else if (stmt instanceof ContinueStatement) {
				return analyzeContinueStatement((ContinueStatement) stmt);
			} else if (stmt instanceof EmptyStatement) {
				StEmptyStatementModel empty = new StEmptyStatementModel();
				empty.setId(idCounter.getNextId());
				empty.setLineNumber(compilationUnit.getLineNumber(stmt
						.getStartPosition()));
				return empty;
			}
			throw new RuntimeException(
					"The stmt type has not been supported yet stmt: "
							+ stmt.getClass() + ", " + stmt.toString());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
			// Expression�̕��őΉ� 2012.11.23 #matsuzawa 2.10.15
			// // StSpecialBlockModel special = new
			// // StSpecialBlockModel(stmt.toString());
			// // #matsuzawa SpExpression�ɓ��� 2012.11.12
			// ExSpecialExpressionModel special = new ExSpecialExpressionModel(
			// stmt.toString());
			// special.setId(idCounter.getNextId());
			// StExpressionModel stex = new StExpressionModel(special);
			// stex.setId(idCounter.getNextId());
			// return stex;
		}
	}

	private StatementModel analyzeReturnStatement(ReturnStatement stmt) {
		StReturnModel model = new StReturnModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(stmt
				.getStartPosition()));
		if (stmt.getExpression() != null) {
			model.setReturnValue(parseExpression(stmt.getExpression()));
		}
		return model;
	}

	private StatementModel analyzeBreakStatement(BreakStatement stmt) {
		StBreakStatementModel model = new StBreakStatementModel("break");
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(stmt
				.getStartPosition()));
		return model;
	}

	private StatementModel analyzeContinueStatement(ContinueStatement stmt) {
		StBreakStatementModel model = new StBreakStatementModel("continue");
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(stmt
				.getStartPosition()));
		return model;
	}

	/**
	 * �u���b�N�̒��g�̉�́i"{" �� "}"�ň͂܂ꂽStatement�j
	 * 
	 * @param block
	 *            �FBlock�m�[�h
	 * @return Block�̉�͌���
	 */
	private StBlockModel parseBlockStatement(Block block) {
		StBlockModel model = new StBlockModel();

		List<?> statements = block.statements();
		// ���ׂẴ��\�b�h���̃X�e�[�g�����g�ɑ΂��ĉ�͂��A�X�e�[�g�����g�u���b�N���f���։�����
		for (Object each : statements) {
			Statement child = (Statement) each;
			model.addElement(parseStatement(child));
		}
		return model;
	}

	/**
	 * ���ۉ��u���b�N�̉��
	 * 
	 * @param block
	 * @return
	 */
	private StAbstractionBlockModel parseAbstractionBlock(Block block) {
		StAbstractionBlockModel model = new StAbstractionBlockModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(block
				.getStartPosition()));
		for (int i = 1; i <= 2; i++) {
			if (abstractionComments.get(block.getStartPosition() + i) != null) {
				String aComments = abstractionComments.get(block
						.getStartPosition() + i);
				if (aComments.startsWith(BlockConverter.COLLAPSED_BLOCK_LABEL)) {
					aComments = aComments
							.substring(BlockConverter.COLLAPSED_BLOCK_LABEL
									.length());
					model.setCollapsed(true);
				}
				model.setCommnent(aComments);
			}
		}
		List<?> statements = block.statements();
		for (Object each : statements) {
			Statement child = (Statement) each;
			model.addChild(parseStatement(child));
		}
		if (statements.size() > 0) {
			model.getChild(0).setParent(model);
		}
		return model;
	}

	/**
	 * If���̉��
	 * 
	 * @param node
	 *            �FIfStatement�m�[�h
	 * @return IfStatement�̉�͌���
	 */
	public StIfElseModel parseIfStatement(IfStatement node) {
		StIfElseModel model = new StIfElseModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		Expression testClause = node.getExpression();
		if (testClause != null) {
			model.setTestClause(parseExpression(testClause));
		}
		Statement thenClause = node.getThenStatement();
		if (thenClause != null) {
			// #matsuzawa 2012.10.23 �����C�P���ɑΉ�
			model.setThenClause(getStBlock(parseStatement(thenClause)));
		}
		Statement elseClause = node.getElseStatement();
		if (elseClause != null) {
			// model.setElseClause((StBlockModel) parseStatement(elseClause));
			// 2012.09.25 #matsuzawa else if���ɑΉ�
			model.setElseClause(getStBlock(parseStatement(elseClause)));
		}

		return model;
	}

	public StWhileModel parseWhileStatement(WhileStatement node) {
		Expression testClause = node.getExpression();
		Statement bodyClause = node.getBody();
		return createWhileStatement(testClause, bodyClause, false);
	}

	public StWhileModel parseDoStatement(DoStatement node) {
		Expression testClause = node.getExpression();
		Statement bodyClause = node.getBody();
		return createWhileStatement(testClause, bodyClause, true);
	}

	private StWhileModel createWhileStatement(Expression testClause,
			Statement bodyClause, boolean isDo) {
		StWhileModel model = new StWhileModel(isDo);
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(testClause
				.getStartPosition()));
		if (testClause != null) {
			model.setTestClause(parseExpression(testClause));
		}

		if (bodyClause != null) {
			model.setBodyClause(getStBlock(parseStatement(bodyClause)));
		}
		return model;
	}

	// �P���������������ɕϊ�����
	private StBlockModel getStBlock(StatementModel model) {
		if (model instanceof StBlockModel) {
			return (StBlockModel) model;
		} else {
			StBlockModel stblock = new StBlockModel();
			stblock.addElement(model);
			return stblock;
		}
	}

	/**
	 * For���̉��
	 * 
	 * @param stmt
	 *            :ForStatement
	 * @return ForStatement�̉�͌���
	 */
	// private StBlockModel parseForStatement(ForStatement node) {
	private StatementModel parseForStatement(ForStatement node) {
		// StBlockModel block = new StBlockModel();
		// block.setId(idCounter.getNextId());

		StAbstractionBlockModel block = new StAbstractionBlockModel();
		block.setCommnent("for");
		block.setId(idCounter.getNextId());
		block.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// Initializer
		for (Object o : node.initializers()) {
			Expression exp = (Expression) o;
			StVariableDeclarationModel vmodel = parseVariableDeclarationExpression((VariableDeclarationExpression) exp);
			block.addChild(vmodel);
		}

		{
			// While �{��
			Expression testClause = node.getExpression();
			Statement bodyClause = node.getBody();
			StWhileModel whileModel = createWhileStatement(testClause,
					bodyClause, false);

			// // Updater
			for (Object o : node.updaters()) {
				Expression exp = (Expression) o;
				ExpressionModel model = parseExpression(exp);
				whileModel.getBodyClause().addElement(model);
			}

			block.addChild(whileModel);
		}

		return block;

		// StLocalVariableModel model = new StLocalVariableModel();
		// model.setId(idCounter.getNextId());
		//
		// model.setName("hoge");
		// model.setType("int");
		// model.setId(idCounter.getNextId());
		// resolver.addLocalVariable(model);
		//
		// return model;

		// //Initialize
		// List<?> fragments = node.initializers();
		// for(Object initialize : fragments){
		// currentBlockStatement.addElement(parseExpression((Expression)
		// initialize));
		// }
		//
		// WhileStatementModel j2b.model = new WhileStatementModel();
		// j2b.model.setId(idCounter.getIdCounter());
		// currentStatement.setNext(j2b.model);
		// j2b.model.setPrevious(currentStatement);
		// currentStatement = j2b.model;
		// //Expression
		// Expression testClause = node.getExpression();
		// if (testClause != null) {
		// j2b.model.setTestClause(parseExpression(testClause));
		// }
		//
		// //Body
		// Statement bodyClause = node.getBody();
		// if (bodyClause != null) {
		// j2b.model.setThenClause(parseStatement(bodyClause));
		//
		// if(j2b.model.getBodyClause() instanceof BlockStatementModel){
		// currentStatement =
		// (((BlockStatementModel)j2b.model.getBodyClause()).getLastChild());
		// }
		//
		// //Update
		// List<?> updaters = node.updaters();
		// for(Object update : updaters){
		// if(update instanceof PostfixExpressionModel){
		// ElementModel updateEx = parseExpression((Expression)update);
		// currentBlockStatement.addElement(updateEx);
		// if(currentStatement instanceof ExpressionStatementModel){
		// ((ExpressionStatementModel)
		// currentStatement).getModel().setNext(updateEx);
		// }
		// currentStatement.setNext(updateEx);
		// }else if(update instanceof Assignment){
		// currentBlockStatement.addElement(
		// new ExpressionStatementModel(
		// parseExpression((Expression)update)));
		// }
		// }
		// }
		// return j2b.model;
	}

	/**
	 * ���i���Ƃ��āj�̉��
	 * 
	 * @param stmt
	 *            �FExpression�m�[�h
	 * @return ExpressionStatement�̉�͌���
	 */
	private StExpressionModel parseExpressionStatement(ExpressionStatement stmt) {
		return new StExpressionModel(parseExpression(stmt.getExpression()));
	}

	/**
	 * �ϐ��錾�̉��
	 * 
	 * @param node
	 *            �FVariableDeclarationStatement�m�[�h
	 * @return j2b.model:VariableDeclarationStatement�̉�͌���
	 */
	public StatementModel parseVariableDeclarationStatement(
			VariableDeclarationStatement node) {
		if (node.fragments().size() > 1) {
			throw new RuntimeException(
					"Two or more do not make a variable declaration simultaneously. ");
		}

		// // int i,j,k; �̂悤�ȏ��������p�[�X����
		// BlockStatementModel block = new BlockStatementModel();
		// List<?> fragments = node.fragments();
		// for (int i = 0; i < fragments.size(); i++) {

		String typeString = typeString(node.getType());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) node
				.fragments().get(0);
		StatementModel model = createLocalVariableModel(typeString, fragment
				.getName().toString(), fragment.getInitializer(), false);
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		return model;
	}

	private StLocalVariableModel parseVariableDeclarationExpression(
			VariableDeclarationExpression node) {
		if (node.fragments().size() > 1) {
			throw new RuntimeException(
					"Two or more do not make a variable declaration simultaneously. ");
		}

		String typeString = typeString(node.getType());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) node
				.fragments().get(0);
		StLocalVariableModel model = createLocalVariableModel(typeString,
				fragment.getName().toString(), fragment.getInitializer(), false);
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		return model;
	}

	private String typeString(Type type) {
		String typeString = type.toString();
		if (type instanceof ParameterizedType) {
			typeString = typeString.replaceAll("<", "��");
			typeString = typeString.replaceAll(">", "��");
			// �ЂƂ܂��C��i�̂�
			// ParameterizedType pType = ((ParameterizedType) type);
			// Type baseType = pType.getType();
			// typeString = baseType.toString();
		}
		return typeString;
	}

	private StLocalVariableModel createLocalVariableModel(String type,
			String name, Expression initializer, boolean argument) {
		// create local variable
		StLocalVariableModel model = new StLocalVariableModel(argument);
		model.setId(idCounter.getNextId());
		// String name = fragment.getName().toString();
		model.setName(name);
		model.setType(type);
		model.setId(idCounter.getNextId());
		variableResolver.addLocalVariable(model);

		// int x = 3;�̂悤�ɁCinitializer�����Ă���ꍇ
		if (initializer != null) {
			model.setInitializer(parseExpression(initializer));
		}

		return model;
	}

	/************************************************************
	 * �ȉ��CExpression�̉�͌n
	 ************************************************************/
	/**
	 * �eExpression�ւ̌o�R�֐�
	 * 
	 * @param node
	 *            �FExpression�m�[�h
	 * @return �eExpression�̉�͌���
	 */
	public ExpressionModel parseExpression(Expression node) {
		try {
			if (node instanceof InfixExpression) {
				return (ExpressionModel) parseInfixExpression((InfixExpression) node);
			} else if (node instanceof NumberLiteral
					|| node instanceof BooleanLiteral
					|| node instanceof StringLiteral) {
				return (ExpressionModel) parseLeteralExpression(node);
			} else if (node instanceof SimpleName) {
				ExpressionModel model = (ExpressionModel) parseVariableGetterExpression(((SimpleName) node)
						.toString());
				model.setLineNumber(compilationUnit.getLineNumber(node
						.getStartPosition()));
				return model;
			} else if (node instanceof Assignment) {
				return parseAssignementExpression((Assignment) node);
			} else if (node instanceof MethodInvocation) {
				return parseMethodInvocationExpression((MethodInvocation) node);
			} else if (node instanceof PostfixExpression) {
				return (ExpressionModel) parsePostfixExpression((PostfixExpression) node);
			} else if (node instanceof PrefixExpression) {
				return (ExpressionModel) parsePrefixExpression((PrefixExpression) node);
			} else if (node instanceof QualifiedName) {
				return (ExpressionModel) parseQualifiedName((QualifiedName) node);
			} else if (node instanceof ParenthesizedExpression) {
				return (ExpressionModel) parseParenthesizedExpression((ParenthesizedExpression) node);
			} else if (node instanceof ClassInstanceCreation) {
				return (ExpressionModel) parseClassInstanceCreation((ClassInstanceCreation) node);
			} else if (node instanceof CastExpression) {
				return (ExpressionModel) parseCastExpression((CastExpression) node);
			}
			throw new RuntimeException(
					"The node type has not been supported yet node: "
							+ node.getClass() + ", " + node.toString());
		} catch (Exception ex) {
			// ex.printStackTrace();
			ExSpecialExpressionModel special = new ExSpecialExpressionModel(
					node.toString());
			special.setId(idCounter.getNextId());
			special.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			return special;
		}
	}

	/**
	 * ������̉��
	 * 
	 * @param node
	 *            :Assignment�m�[�h
	 * @return Assignment�̉�͌���
	 */
	private ExpressionModel parseAssignementExpression(Assignment node) {
		ExVariableSetterModel model = new ExVariableSetterModel();
		ExpressionModel rightExpression = parseExpression(node
				.getRightHandSide());
		model.setRightExpression(rightExpression);
		if (node.getRightHandSide() instanceof Assignment) {
			throw new RuntimeException("not supported two or more substitution");
		}
		String name = node.getLeftHandSide().toString();
		model.setVariable(variableResolver.resolve(name));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		return model;
	}

	/**
	 * �C���N�������g�A�f�N�������g�����
	 * 
	 * @param node
	 *            :PostfixExpression�m�[�h
	 * @return PostfixExpression�̉�͌���
	 */
	private ElementModel parsePostfixExpression(PostfixExpression node) {
		ExPostfixModel model = new ExPostfixModel();
		String name = node.getOperand().toString();
		model.setVariable(variableResolver.resolve(name));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		ExLeteralModel leteral = new ExLeteralModel();
		leteral.setType("number");
		if (node.getOperator().toString().equals("++")) {
			leteral.setValue("1");
		} else if (node.getOperator().toString().equals("--")) {
			leteral.setValue("-1");
		}
		leteral.setId(idCounter.getNextId());
		leteral.setParent(model);
		leteral.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		model.setPostfix(leteral);

		return model;
	}

	/**
	 * �}�C�i�X�̐��l�̉��
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parsePrefixExpression(PrefixExpression node) {
		if (node.getOperator().toString().equals("!")) {
			ExNotModel model = new ExNotModel();
			model.setId(idCounter.getNextId());
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			model.setExpression(parseExpression(node.getOperand()));
			return model;
		}
		if (node.getOperator().toString().equals("-")) {
			ExLeteralModel model = new ExLeteralModel();
			model.setId(idCounter.getNextId());
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			model.setType(getLeteralType(node.getOperand()));
			model.setValue("-" + node.getOperand());
			return model;
		}

		throw new RuntimeException("not supported: " + node.toString());
	}

	public ExpressionModel parseMethodInvocationExpression(MethodInvocation node) {
		// ��O
		String fullName = node.toString();
		// System.out.println("recv: " + fullName);
		if (fullName.startsWith("System.out.print(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setName("cui-print");
			return callMethod;
		} else if (fullName.startsWith("System.out.println(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setName("cui-println");
			return callMethod;
		} else if (fullName.startsWith("Math.random(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("cui-random");
			return callMethod;
		} else if (fullName.startsWith("Math.sqrt(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("sqrt");
			return callMethod;
		} else if (fullName.startsWith("Math.sin(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("sin");
			return callMethod;
		} else if (fullName.startsWith("Math.cos(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("cos");
			return callMethod;
		} else if (fullName.startsWith("Math.tan(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("tan");
			return callMethod;
		} else if (fullName.startsWith("Math.log(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("log");
			return callMethod;
		} else if (fullName.startsWith("Math.toRadians(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("toRadians");
			return callMethod;
		} else if (fullName.startsWith("Integer.parseInt(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("int");
			callMethod.setName("toIntFromString");
			return callMethod;
		} else if (fullName.startsWith("Integer.toString(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("string");
			callMethod.setName("toStringFromInt");
			return callMethod;
		} else if (fullName.startsWith("Double.parseDouble(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("double");
			callMethod.setName("toDoubleFromString");
			return callMethod;
		} else if (fullName.startsWith("Double.toString(")) {
			ExCallMethodModel callMethod = parseMethodCallExpression(node);
			callMethod.setType("string");
			callMethod.setName("toStringFromDouble");
			return callMethod;
		} else if (fullName.endsWith("hashCode()")) {
			Expression receiver = node.getExpression();
			if (receiver != null) {
				ExpressionModel expModel = parseExpression(receiver);
				ExCallMethodModel callMethod = parseMethodCallExpression(node);
				callMethod.addArgument(expModel);
				callMethod.setType("int");
				callMethod.setName("hashCode");
				return callMethod;
			}
		} else if (fullName.indexOf(".equals(") != -1
				&& node.arguments().size() == 1) {// �ЂƂ܂��Cstring�^���Ǝv�����Ƃɂ���D�I�u�W�F�N�g�^�͌�񂵁i���{�I�ȉ������K�v�j�D#matsuzawa
													// 2012.11.23
			Expression receiver = node.getExpression();
			Expression arg = (Expression) node.arguments().get(0);
			ExInfixModel model = new ExInfixModel();
			model.setId(idCounter.getNextId());
			model.setLineNumber(compilationUnit.getLineNumber(node
					.getStartPosition()));
			model.setOperator("equals");
			model.setLeftExpression(parseExpression(receiver));
			model.setRightExpression(parseExpression(arg));
			return model;
		}

		if (methodResolver.isRegistered(node)) {
			// System.out.println("methodinvoke: " + node.toString());
			Expression receiver = node.getExpression();
			if (receiver == null) {
				return parseMethodCallExpression(node);
			}

			// �R�R�����}���u
			if (receiver instanceof MethodInvocation) {
				ExpressionModel receiverModel = parseMethodInvocationExpression((MethodInvocation) receiver);
				return parseCallActionMethodExpression2(node, receiverModel);
			}

			ExVariableGetterModel receiverModel = null;
			StVariableDeclarationModel variable = variableResolver
					.resolve(receiver.toString());
			if (variable != null) {
				receiverModel = parseVariableGetterExpression(variable
						.getName());
				receiverModel.setLineNumber(compilationUnit
						.getLineNumber(receiver.getStartPosition()));
			}
			return parseCallActionMethodExpression2(node, receiverModel);

			// if (methodResolver.getReturnType(node) == ExpressionModel.VOID) {
			// return parseCallActionMethodExpression(node);
			// } else {
			// return parseCallGetterMethodExpression(node);
			// }
		}

		// This method is not registered.
		ExSpecialExpressionModel sp = new ExSpecialExpressionModel(
				node.toString());
		// sp.setType(getType(node));
		sp.setId(idCounter.getNextId());
		sp.setLineNumber(compilationUnit.getLineNumber(node.getStartPosition()));
		return sp;
	}

	// private String getType(Expression exp) {
	// System.out.println("resolveTypeBinding(): " + exp.resolveTypeBinding());
	// // System.out.println(exp.resolveTypeBinding().getElementType());
	// return exp.resolveTypeBinding().getElementType().getName();
	// }

	/**
	 * 
	 * @param node
	 *            :MethodInvocation�m�[�h
	 * @return MethodInvocation�̉�͌���
	 */
	public ExCallMethodModel parseMethodCallExpression(MethodInvocation node) {
		ExCallMethodModel model;
		if (methodResolver.isRegisteredAsUserMethod(node)) {
			model = new ExCallUserMethodModel();
			model.setArgumentLabels(methodResolver.getArgumentLabels(node));
		} else {
			model = new ExCallMethodModel();
		}

		String name = node.getName().toString();
		model.setName(name);
		model.setType(methodResolver.getReturnType(node));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// ����
		for (int i = 0; i < node.arguments().size(); i++) {
			ExpressionModel arg = parseExpression((Expression) node.arguments()
					.get(i));
			// if ("print".equals(model.getName()) &&
			// numberRelationChecker(arg)) {
			// arg = typeChangeModelCreater(arg);
			// }
			model.addArgument(arg);
		}
		return model;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public ExCallActionMethodModel parseCallActionMethodExpression(
			MethodInvocation node) {
		ExCallActionMethodModel model = new ExCallActionMethodModel();
		String receiver = node.getExpression().toString();
		StVariableDeclarationModel variable = variableResolver
				.resolve(receiver);
		if (variable == null) {
			variable = new StVariableDeclarationModel();// dummy
			variable.setName(receiver.toString());
			variable.setType("void");
		}
		model.setVariable(variable);
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// #matsuzawa 2012.11.13 �������{�I�Ȏ��Â��K�v�I
		StBlockModel block = new StBlockModel();
		block.setId(model.getId());
		block.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// if (node.getExpression() instanceof QualifiedName) {
		// throw new RuntimeException("not supported two or more substitution");
		// }
		block.addElement(parseMethodCallExpression(node));
		model.setCallMethod(block);

		// // �ȉ��C���V������
		// if (node.getExpression() instanceof QualifiedName) {
		// throw new RuntimeException("not supported two or more substitution");
		// }
		//
		// CallMethodModel method = parseMethodCallExpression(node);
		// method.setId(model.getId());
		// model.setCallMethod(method);
		// // �ȏ�C���V������
		return model;
	}

	public ExCallActionMethodModel2 parseCallActionMethodExpression2(
			MethodInvocation node, ExpressionModel receiverModel) {
		ExCallActionMethodModel2 model = new ExCallActionMethodModel2();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		ExCallMethodModel callMethod = parseMethodCallExpression(node);
		model.setReceiver(receiverModel);
		model.setCallMethod(callMethod);
		// ���callmethod��plug�̌^���Z�b�g���Ƃ�
		if (callMethod instanceof ExpressionModel
				&& !((ExpressionModel) callMethod).getType().equals("void")) {
			model.setType(ElementModel
					.convertJavaTypeToBlockType(((ExpressionModel) callMethod)
							.getType()));
		}
		return model;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public ExCallGetterMethodModel parseCallGetterMethodExpression(
			MethodInvocation node) {
		ExCallGetterMethodModel model = new ExCallGetterMethodModel();
		String name = node.getExpression().toString();
		model.setVariable(variableResolver.resolve(name));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		if (node.getExpression() instanceof QualifiedName) {
			throw new RuntimeException("not supported two or more substitution");
		}
		model.setArgument(parseMethodCallExpression(node));
		return model;
	}

	// /**
	// *
	// * @param MethodInvocation�m�[�h
	// * @return MethodInvocation�m�[�h�̉�͌���
	// */
	// public ExAssignmentCallMethodModel parseAssignmentCallExpression(
	// MethodInvocation node) {
	// ExAssignmentCallMethodModel model = new ExAssignmentCallMethodModel();
	// model.setName(node.getName().toString());
	// model.setId(idCounter.getNextId());
	// // ����
	// for (int i = 0; i < node.arguments().size(); i++) {
	// ExpressionModel arg = parseExpression((Expression) node.arguments()
	// .get(i));
	// model.addArgument(arg);
	// }
	// return model;
	// }

	/**
	 * �萔�̉��
	 * 
	 * @param node
	 *            :Expression�m�[�h
	 * @return Expression�̉�͌���
	 */
	public ExLeteralModel parseLeteralExpression(Expression node) {
		ExLeteralModel model = new ExLeteralModel();
		model.setType(getLeteralType(node));
		model.setValue(node.toString());
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		return model;
	}

	/**
	 * �萔�̌^�̉��
	 * 
	 * @param leteral
	 *            :Experssion�m�[�h
	 * @return ��͂����萔�̌^
	 */
	private String getLeteralType(Expression leteral) {
		if (leteral instanceof NumberLiteral) {
			if (leteral.toString().contains(".")
					|| leteral.toString().endsWith("d")) {
				return "double-number";
			} else {
				return "number";
			}
		} else if (leteral instanceof BooleanLiteral) {
			return "boolean";
		} else if (leteral instanceof StringLiteral) {
			return "string";
		}

		throw new RuntimeException("not supported");
	}

	/**
	 * �v�Z���̉��
	 * 
	 * @param node
	 *            :InfixExpression�m�[�h
	 * @return InfixExpression�̉�͌���
	 */
	public ExInfixModel parseInfixExpression(InfixExpression node) {
		String oparator = node.getOperator().toString();
		// String type = infixTypeChecker(node);

		ExInfixModel model = new ExInfixModel();
		model.setOperator(oparator);
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// model.setLeftExpression(numberToStringConverter(type,parseExpression(node.getLeftOperand())));
		// model.setRightExpression(numberToStringConverter(type,parseExpression(node.getRightOperand())));
		model.setLeftExpression(parseExpression(node.getLeftOperand()));
		model.setRightExpression(parseExpression(node.getRightOperand()));

		// �������@�ǉ���
		for (Object o : node.extendedOperands()) {
			Expression exp = (Expression) o;
			ExInfixModel newmodel = new ExInfixModel();
			newmodel.setId(idCounter.getNextId());
			newmodel.setLineNumber(compilationUnit.getLineNumber(exp
					.getStartPosition()));
			newmodel.setOperator(oparator);
			newmodel.setLeftExpression(model);
			// newmodel.setRightExpression(numberToStringConverter(type,
			// parseExpression(exp)));
			newmodel.setRightExpression(parseExpression(exp));
			model = newmodel;
		}

		// string �� == ��r�`�F�b�N
		if (model.getSocketType().equals("string")
				&& (model.getOperator().equals("==") || model.getOperator()
						.equals("!="))) {
			throw new RuntimeException("string type is compared by == or !=");
		}

		// ExInfixModel model = new ExInfixModel();
		// model.setOperator(node.getOperator().toString());
		// model.setId(idCounter.getNextId());
		// model.setLeftExpression(numberToStringConverter(infixTypeChecker(node),
		// parseExpression(node.getLeftOperand())));
		// model.setRightExpression(numberToStringConverter(
		// infixTypeChecker(node), parseExpression(node.getRightOperand())));
		return model;
	}

	// private String infixTypeChecker(InfixExpression node) {
	// if (checkOperandStringType(node.getLeftOperand())
	// || checkOperandStringType(node.getRightOperand())) {
	// return "string";
	// }
	// return "int";
	// }

	// private boolean checkOperandStringType(Expression operand) {
	// if (operand instanceof StringLiteral) {
	// return true;
	// }
	// if (operand instanceof InfixExpression) {
	// return checkOperandStringType(((InfixExpression) operand)
	// .getLeftOperand())
	// || checkOperandStringType(((InfixExpression) operand)
	// .getRightOperand());
	// }
	// return false;
	// }

	// �ȉ��C�����ł��d������Ȃ��̂ō폜 #matsuzawa 2012.11.14
	// private ExpressionModel numberToStringConverter(String type,
	// ExpressionModel initializer) {
	// if (("String".equals(type) || "string".equals(type))
	// && numberRelationChecker(initializer)) {
	// return typeChangeModelCreater(initializer);
	// }
	//
	// return initializer;
	// }

	// private boolean numberRelationChecker(ExpressionModel initializer) {
	// if (initializer instanceof ExInfixModel) {
	// String type = ((ExInfixModel) initializer).getType();
	// if ("number".equals(type)) {
	// return true;
	// }
	// } /*
	// * else if (initializer instanceof ExAssignmentCallMethodModel) { String
	// * name = ((ExAssignmentCallMethodModel) initializer).getName(); if
	// * ("random".equals(name)) { return true; } }
	// */
	// else if (initializer instanceof ExCallMethodModel) {
	// String name = ((ExCallMethodModel) initializer).getName();
	// if ("random".equals(name)) {
	// return true;
	// }
	// } else if (initializer instanceof ExLeteralModel) {
	// String type = ((ExLeteralModel) initializer).getType();
	// if ("number".equals(type)) {
	// return true;
	// }
	// } else if (initializer instanceof ExVariableGetterModel) {
	// String type = ((ExVariableGetterModel) initializer).getType();
	// if ("int".equals(type) || "double".equals(type)) {
	// type = "number";
	// }
	// if ("number".equals(type)) {
	// return true;
	// }
	// }
	// return false;
	// }

	// private ExpressionModel typeChangeModelCreater(ExpressionModel argument)
	// {
	// ExCastModel model = new ExCastModel();
	// model.setType("toString");
	// model.setId(idCounter.getNextId());
	// // ����
	// model.addArgument(argument);
	// return model;
	// }

	/**
	 * �ۊ��ʂ̉��
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parseParenthesizedExpression(
			ParenthesizedExpression node) {
		return parseExpression(node.getExpression());
	}

	/**
	 * �ϐ��Ăяo���̉��
	 * 
	 * @param node
	 *            :SimpleName�m�[�h
	 * @return SimpleName�m�[�h�̉��
	 */
	public ExVariableGetterModel parseVariableGetterExpression(String name) {
		ExVariableGetterModel model = new ExVariableGetterModel();
		model.setVariable(variableResolver.resolve(name));
		model.setId(idCounter.getNextId());
		return model;
	}

	/**
	 * @param node
	 * @return
	 */
	private ExpressionModel parseQualifiedName(QualifiedName node) {
		ExLeteralModel model = new ExLeteralModel();
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// #matsuzawa 2012.11.24
		if (isColorBlock(node)) {
			model.setType("color");
			model.setValue(node.getName().toString());
			return model;
		}
		// #matsuzawa 2012.11.12
		// model.setType("number");
		// model.setValue(node.getName().toString());
		model.setType("poly");
		model.setValue(node.getFullyQualifiedName().toString());

		return model;
	}

	private boolean isColorBlock(QualifiedName node) {
		String fullName = node.getFullyQualifiedName();
		return fullName.startsWith("java.awt.Color.");
		// for (String colorName : BlockConverter.COLOR_NAMES) {
		// if (value.endsWith(colorName)) {
		// return true;
		// }
		// }
		// return false;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parseClassInstanceCreation(
			ClassInstanceCreation node) {
		ExClassInstanceCreationModel model = new ExClassInstanceCreationModel();
		model.setValue(typeString(node.getType()));
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// ����
		for (int i = 0; i < node.arguments().size(); i++) {
			ExpressionModel arg = parseExpression((Expression) node.arguments()
					.get(i));
			model.addArgument(arg);
		}
		return model;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private ExpressionModel parseCastExpression(CastExpression node) {
		ExCastModel model = new ExCastModel();
		model.setType(node.getType().toString());
		model.setId(idCounter.getNextId());
		model.setLineNumber(compilationUnit.getLineNumber(node
				.getStartPosition()));
		// ����
		model.addArgument(parseExpression(node.getExpression()));
		return model;
	}
}
