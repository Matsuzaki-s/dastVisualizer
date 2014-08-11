package tea.analytics;

import generef.analytics.FailureKnowledgeAnalyzer;
import generef.knowledge.RSFailureKnowledge;

import java.util.ArrayList;
import java.util.List;

import pres.loader.logmodel.PLLog;
import pres.loader.model.PLFile;
import pres.loader.utils.PLWorkingTime;
import pres.loader.utils.PLWorkingTimeCalculator;
import tea.analytics.model.TCompileErrorHistory;
import tea.analytics.model.TCompileErrorHistorySegment;
import tea.analytics.model.TCompilePoint;
import clib.common.compiler.CCompileResult;
import clib.common.compiler.CDiagnostic;
import clib.common.time.CTime;
import clib.common.time.CTimeInterval;
import clib.common.time.CTimeOrderedList;
import clib.common.utils.ICChecker;

public class CompileErrorAnalyzer {

	private static int P1 = 1;
	private static int P2A = 2;
	private static int P2B = 3;
	private static int P3A = 4;
	private static int P3B = 5;
	private static int P4 = 6;
	private static int P5 = 7;

	private PLFile file = null;
	private FailureKnowledgeAnalyzer fkAnalyzer;

	private List<TCompileErrorHistory> histories;
	private List<TCompilePoint> compilePoints;

	/****************************************************
	 * Constructor
	 ****************************************************/

	public CompileErrorAnalyzer() {
	}

	public CompileErrorAnalyzer(PLFile file) {
		this.file = file;
	}

	public CompileErrorAnalyzer(PLFile file, FailureKnowledgeAnalyzer fkAnalyzer) {
		this.file = file;
		this.fkAnalyzer = fkAnalyzer;
	}

	public void analyze() {
		List<TCompilePoint> compilePoints = buildCompilePoints();
		analyze(compilePoints);
	}

	public void analyze(List<TCompilePoint> compilePoints) {
		histories = new ArrayList<TCompileErrorHistory>();
		this.compilePoints = compilePoints;
		buildSegments();
		buildHistories();
		setA();
		setPattern();
	}

	private List<TCompilePoint> buildCompilePoints() {
		List<TCompilePoint> compilePoints = new ArrayList<TCompilePoint>();

		TCompilePoint previous = null;
		for (PLLog compileLog : getCompileLogs()) {
			CTime time = compileLog.getTime();
			CCompileResult result = file.getProject().getCompileResult(time);
			TCompilePoint point = new TCompilePoint(time, result);
			point.setPrevious(previous);
			compilePoints.add(point);
			previous = point;
		}

		return compilePoints;
	}

	/**
	 * �����file�̃R���p�C�����O��Ԃ��܂�
	 * 
	 * @param file
	 * @return
	 */
	private CTimeOrderedList<PLLog> getCompileLogs() {
		CTimeOrderedList<PLLog> logs = file.getOrderedLogs();
		CTimeOrderedList<PLLog> compileLogs = logs
				.select(new ICChecker<PLLog>() {
					public boolean check(PLLog t) {
						return "COMMAND_RECORD".equals(t.getType())
								&& t.getSubType().equals("COMPILE");
					}
				});
		return compileLogs;
	}

	private void buildSegments() {

		PLWorkingTime workingTime = null;
		if (file != null) {
			workingTime = new PLWorkingTimeCalculator().calculate(file);
		}

		for (int i = 1; i < compilePoints.size(); i++) {
			TCompilePoint point = compilePoints.get(i);

			List<CDiagnostic> diagnostics = new ArrayList<CDiagnostic>(point
					.getCompileResult().getDiagnostics());

			for (CDiagnostic diagnostic : point.getPrevious()
					.getCompileResult().getDiagnostics()) {
				TCompileErrorHistorySegment segment = new TCompileErrorHistorySegment(
						diagnostic, point.getPrevious(), point);
				segment.setFixed(isFixed(diagnostic, diagnostics));// ��--�� or
																	// ��--��
				point.getPrevious().addBeginningSegment(segment);
				point.addFinishedSegment(segment);

				segment.setWorking(workingTime);

				if (file != null) { // GeneRef�ł̕��͎��͏������Ȃ�
					RSFailureKnowledge knowledge = getContainsFailureKnowledge(segment);
					if (knowledge != null) {
						segment.setGeneRefTime(knowledge.getWindowOpenTime(),
								knowledge.getWindowCloseTime());
					}
				}
			}
		}

	}

	private boolean isFixed(CDiagnostic diagnostic,
			List<CDiagnostic> diagnostics) {

		for (CDiagnostic another : diagnostics) {
			if (diagnostic.isSameKind(another)) {
				diagnostics.remove(another);
				return false;
			}
		}
		return true;
	}

	private RSFailureKnowledge getContainsFailureKnowledge(
			TCompileErrorHistorySegment segment) {
		if (fkAnalyzer == null) {
			return null;
		}

		for (RSFailureKnowledge knowledge : fkAnalyzer
				.getWritingPointKnowledges()) {
			if (checkGeneRefOpen(segment, knowledge)) { // �R���p�C������5�b�ȓ���GeneRef���\�����ꂽ���ǂ���
				return knowledge;
			}
		}
		return null;
	}

	private boolean checkGeneRefOpen(TCompileErrorHistorySegment segment,
			RSFailureKnowledge knowledge) {
		long startTime = segment.getStart().getTime().getAsLong();
		long endTime = segment.getEnd().getTime().getAsLong();
		long startGeneRefTime = knowledge.getWindowOpenTime();
		long endGeneRefTime = knowledge.getWindowCloseTime();

		long time = startGeneRefTime - startTime;
		if ((0 <= time && time < 5000) && endGeneRefTime < endTime) {
			return true;
		}
		return false;
	}

	private void buildHistories() {
		List<TCompileErrorHistory> nonfixedErrors = new ArrayList<TCompileErrorHistory>();
		List<TCompileErrorHistory> appeared = new ArrayList<TCompileErrorHistory>();
		for (TCompilePoint currentPoint : compilePoints) {
			for (TCompileErrorHistorySegment segment : currentPoint
					.getBeginningSegments()) {
				TCompileErrorHistory history = getHistory(appeared,
						nonfixedErrors, segment);
				if (history != null) {
					appeared.add(history);
				}
				if (segment.isFixed()) {
					if (history == null) {
						TCompileErrorHistory newHistory = new TCompileErrorHistory();
						segment.setHistory(newHistory);
						newHistory.addSegment(segment);
						histories.add(newHistory);
					} else {
						segment.setHistory(history);
						history.addSegment(segment);
						nonfixedErrors.remove(history);
					}
				} else if (nonfixedErrors.contains(history)) {
					segment.setHistory(history);
					history.addSegment(segment);
				} else {
					TCompileErrorHistory newHistory = new TCompileErrorHistory();
					segment.setHistory(newHistory);
					newHistory.addSegment(segment);
					histories.add(newHistory);
					nonfixedErrors.add(newHistory);
					appeared.add(newHistory);
				}
			}
			appeared.clear();
		}
	}

	private TCompileErrorHistory getHistory(
			List<TCompileErrorHistory> appeared,
			List<TCompileErrorHistory> histories,
			TCompileErrorHistorySegment segment) {
		for (TCompileErrorHistory history : histories) {
			if (!appeared.contains(history)) {
				for (TCompileErrorHistorySegment seg : history.getSegments()) {
					if (seg.getCompileError().isSameKind(
							segment.getCompileError())) {
						return history;
					}
				}
			}
		}
		return null;
	}

	// private void setA() {
	// for (TCompilePoint currentPoint : compilePoints) {
	// List<TCompileErrorHistorySegment> segments;
	// if (currentPoint.hasFixedSegments()) {
	// segments = currentPoint.getFixedSegments();
	// } else {
	// segments = currentPoint.getNonFixedSegments();
	// }
	// double a = 1d / segments.size();
	// for (TCompileErrorHistorySegment segment : segments) {
	// segment.setA(a);
	// }
	// }
	// }

	private void setA() {
		for (TCompilePoint point : compilePoints) {
			if (point.hasFixedSegments()) {
				List<TCompileErrorHistorySegment> fixedSegments = point
						.getFixedSegments();

				for (TCompileErrorHistorySegment fixedSegment : fixedSegments) {
					goBackCompilePoint(fixedSegments, fixedSegment);
				}

				// �R���p�C���|�C���g���`�F�b�N�ς݂ɂ���
				for (TCompileErrorHistorySegment fixedSegment : fixedSegments) {
					for (TCompileErrorHistorySegment segment : fixedSegment
							.getHistory().getSegments()) {
						segment.getStart().setCheck(true);
					}
				}
			}
		}
	}

	/**
	 * �R���p�C���|�C���g��k���Ă��ꂼ��̃Z�O�����g�ɌW����ݒ肷��
	 * 
	 * @param fixedSegments
	 * @param fixedSegment
	 */
	private void goBackCompilePoint(
			List<TCompileErrorHistorySegment> fixedSegments,
			TCompileErrorHistorySegment fixedSegment) {
		for (TCompileErrorHistorySegment segment : fixedSegment.getHistory()
				.getSegments()) {
			TCompilePoint start = segment.getStart();
			if (!start.isCheck()) {
				// if (segment.getCompileError().getErrorMessage()
				// .equals("';' ������܂���B")) {
				// segment.setA(1d / (fixedSegments.size() - getFixedErrorCount(
				// fixedSegments, start)));
				// } else {
				// segment.setA(1d / (fixedSegments.size()
				// - getFixedErrorCount(fixedSegments, start) -
				// getSemicolonCount(fixedSegments)));
				// }
				if (segment.isWorking()) {
					segment.setA(1d / (fixedSegments.size() - getFixedErrorCount(
							fixedSegments, start)));
				} else {
					segment.setA(0d);
				}
			} else {
				segment.setA(0d);
			}
		}
	}

	// private int getSemicolonCount(
	// List<TCompileErrorHistorySegment> fixedSegments) {
	// int count = 0;
	//
	// for (TCompileErrorHistorySegment segment : fixedSegments) {
	// if (segment.getCompileError().getErrorMessage()
	// .equals("';' ������܂���B")) {
	// count++;
	// }
	// }
	//
	// return count;
	// }

	/**
	 * 
	 * <pre>
	 * ��-��-��-��
	 *   ��-��-��
	 *   ��-��-��
	 * 1 3 3
	 * </pre>
	 * 
	 * @param fixedSegments
	 * @param start
	 * @return
	 */
	private int getFixedErrorCount(
			List<TCompileErrorHistorySegment> fixedSegments, TCompilePoint start) {
		int num = 0;
		for (TCompilePoint startPoint : getStartPoints(fixedSegments)) {
			if (start.getTime().before(startPoint.getTime())) {
				num++;
			}
		}
		return num;
	}

	/**
	 * �q�X�g���[�̃X�^�[�g�R���p�C���|�C���g�̃��X�g��Ԃ��܂�
	 * 
	 * @param fixedSegments
	 * @return
	 */
	private List<TCompilePoint> getStartPoints(
			List<TCompileErrorHistorySegment> fixedSegments) {
		List<TCompilePoint> startPoints = new ArrayList<TCompilePoint>();
		for (TCompileErrorHistorySegment fixedSegment : fixedSegments) {
			TCompileErrorHistory history = fixedSegment.getHistory();
			startPoints.add(history.getStart());
		}
		return startPoints;
	}

	private void setPattern() {
		for (TCompilePoint point : compilePoints) {

			if (point.getFixedSegments().size() == 1) {
				TCompileErrorHistory history = point.getFixedSegments().get(0)
						.getHistory();
				if (history.getSegments().size() == 1) {
					point.addPattern(P1);

					if (isPattern2A(point)) {
						point.addPattern(P2A);
					}

					if (isPattern5(point)) {
						point.addPattern(P5);
					}

				} else {
					if (isPattern2A(point)) {
						point.addPattern(P2A);
					}

					if (isPattern3B(point)) {
						point.addPattern(P3B);
					} else {
						point.addPattern(P2B);
					}

					if (isPattern5(point)) {
						point.addPattern(P5);
					}

				}

			} else if (point.getFixedSegments().size() > 1) {
				if (isPattern2A(point)) {
					point.addPattern(P2A);
				}

				if (isSameSegmentCount(point)) {
					point.addPattern(P3A);
				} else {
					point.addPattern(P4);
				}

				if (isPattern3B(point)) {
					point.addPattern(P3B);
				} else {
					if (isPattern1(point)) {
						point.addPattern(P1);
					} else {
						point.addPattern(P2B);
					}
				}

				if (isPattern5(point)) {
					point.addPattern(P5);
				}
			}
		}
	}

	/**
	 * �W��0�ȊO�̃Z�O�����g�����S�Ẵq�X�g���[�ň�v���Ă��邩�Ԃ��܂�
	 * 
	 * @param point
	 * @return
	 */
	private boolean isSameSegmentCount(TCompilePoint point) {
		int size = point.getFixedSegments().size();
		int[] num = new int[size];

		// ���ꂼ��̃q�X�g���[�ɑ΂��ČW��0�������Z�O�����g�����J�E���g
		for (int i = 0; i < size; i++) {
			TCompileErrorHistory history = point.getFixedSegments().get(i)
					.getHistory();
			int temp = 0;
			for (TCompileErrorHistorySegment segment : history.getSegments()) {
				if (segment.getA() != 0) {
					temp++;
				}
			}
			num[i] = temp;
		}

		// �W��0�ȊO�̃Z�O�����g���̔�r
		for (int i = 1; i < size; i++) {
			int temp = num[i - 1];
			if (num[i] != temp) {
				return false;
			}
		}

		return true;
	}

	private boolean isPattern1(TCompilePoint point) {
		for (TCompileErrorHistorySegment segment : point.getFixedSegments()) {
			if (segment.getHistory().getSegments().size() == 1) {
				return true;
			}
		}
		return false;
	}

	private boolean isPattern2A(TCompilePoint point) {
		if (point.getBeginningSegments().size() > point.getFinishedSegments()
				.size() - point.getFixedSegments().size()) {
			String beginErrorKind = point.getBeginningSegments().get(0)
					.getCompileError().getMessageParser().getMessageKind();
			String finishErrorKind = point.getFinishedSegments().get(0)
					.getCompileError().getMessageParser().getMessageKind();
			if (beginErrorKind.equals(finishErrorKind)) {
				return true;
			}
		}
		return false;
	}

	private boolean isPattern3B(TCompilePoint point) {
		for (TCompileErrorHistorySegment segment : point.getFixedSegments()) {
			for (TCompileErrorHistorySegment seg : segment.getHistory()
					.getSegments()) {
				if (seg.getA() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPattern5(TCompilePoint point) {

		if (point.getBeginningSegments().size() == 0) {
			return false;
		}

		String fixedErrorKind = point.getFixedSegments().get(0)
				.getCompileError().getMessageParser().getMessageKind();
		String nonFixedErrorKind = point.getBeginningSegments().get(0)
				.getCompileError().getMessageParser().getMessageKind();

		if (fixedErrorKind.equals("�Ӗ���̓G���[")
				&& nonFixedErrorKind.equals("�\����̓G���[")) {
			return true;
		}

		return false;
	}

	/****************************************************
	 * getter
	 ****************************************************/

	public PLFile getFile() {
		return file;
	}

	public List<TCompileErrorHistory> getHistories() {
		return histories;
	}

	public List<TCompilePoint> getCompilePoints() {
		return compilePoints;
	}

	public CTimeInterval getCorrectionTime() {
		CTimeInterval correctionTime = new CTimeInterval(0);
		for (TCompileErrorHistory history : histories) {
			correctionTime = correctionTime.add(history.getCorrectionTime());
		}
		return correctionTime;
	}

	// public void analyze() {
	// histories = new ArrayList<TCompileErrorHistory>();
	// compilePoints = new ArrayList<TCompilePoint>();
	// buildCompilePoints();
	// buildHistories();
	// setPattern();
	// setA();
	// }
	//
	// private List<TCompileErrorHistory> buildHistories() {
	// TCompilePoint prevCompilePoint = null;
	//
	// List<TCompileErrorHistory> histories = new
	// ArrayList<TCompileErrorHistory>();
	//
	// for (int i = 0; i < compilePoints.size(); i++) {
	// // �R���p�C���|�C���g�̎擾
	// TCompilePoint compilePoint = compilePoints.get(i);
	// TCompilePoint nextCompilePoint;
	// if (i == compilePoints.size() - 1) {
	// nextCompilePoint = null;
	// } else {
	// nextCompilePoint = compilePoints.get(i + 1);
	// }
	//
	// // prevCompilePoint�ɂ���G���[���X�g
	// List<CDiagnostic> prevErrors = null;
	// if (prevCompilePoint != null) {
	// prevErrors = new ArrayList<CDiagnostic>(prevCompilePoint
	// .getResult().getDiagnostics());
	// }
	//
	// for (CDiagnostic compileError : compilePoint.getResult()
	// .getDiagnostics()) {
	//
	// if (prevCompilePoint != null) {
	// CDiagnostic sameError = getContainsSameError(prevErrors,
	// compileError);
	//
	// // �����G���[���O�̃R���p�C���|�C���g�Ŕ������Ă���ꍇ
	// if (sameError != null) {
	//
	// // �����G���[���܂܂�Ă���history��T����segment��ǉ�
	// for (TCompileErrorHistory history : histories) {
	// for (TCompileErrorHistorySegment segment : history
	// .getSegments()) {
	// if (sameError == segment.getCompileError()) {
	// history.addSegment(new TCompileErrorHistorySegment(
	// compileError, compilePoint,
	// nextCompilePoint));
	// break;
	// }
	// }
	// }
	//
	// prevErrors.remove(sameError);
	//
	// } else {
	// histories = createNewHistory(histories,
	// new TCompileErrorHistorySegment(compileError,
	// compilePoint, nextCompilePoint));
	// }
	//
	// } else {
	// histories = createNewHistory(histories,
	// new TCompileErrorHistorySegment(compileError,
	// compilePoint, nextCompilePoint));
	// }
	// }
	//
	// // �C�����ꂽ�G���[�̐ݒ�
	// if (prevCompilePoint == null) {
	// compilePoint.setCorrectionErrors(getCorrectionErrors(null,
	// compilePoint.getResult().getDiagnostics()));
	// } else {
	// compilePoint.setCorrectionErrors(getCorrectionErrors(
	// prevCompilePoint.getResult().getDiagnostics(),
	// compilePoint.getResult().getDiagnostics()));
	// }
	//
	// // �O�̃R���p�C���|�C���g��ێ�
	// prevCompilePoint = compilePoint;
	//
	// }
	//
	// return histories;
	// }
	//
	// /**
	// * �V����CompileErrorHistory�I�u�W�F�N�g�����܂�
	// *
	// * @param histories
	// * @param segment
	// * @return
	// */
	// private List<TCompileErrorHistory> createNewHistory(
	// List<TCompileErrorHistory> histories,
	// TCompileErrorHistorySegment segment) {
	// histories.add(new TCompileErrorHistory());
	// histories.get(histories.size() - 1).addSegment(segment);
	// return histories;
	// }
	//
	// /**
	// * ����̃t�@�C���̃R���p�C���|�C���g��Ԃ��܂�
	// *
	// * @return
	// */
	// private List<TCompilePoint> buildCompilePoints() {
	// List<TCompilePoint> compilePoints = new ArrayList<TCompilePoint>();
	//
	// for (PLLog compileLog : getCompileLogs(file)) {
	// CTime time = compileLog.getTime();
	// CCompileResult result = file.getProject().getCompileResult(time);
	// compilePoints.add(new TCompilePoint(time, result));
	// }
	//
	// return compilePoints;
	// }
	//
	// /**
	// * ���X�g�̒��ɓ����G���[���܂܂�Ă��邩���G���[���b�Z�[�W�ƃV���{���Ŕ��肷��
	// *
	// * @param compileErrors
	// * �G���[���X�g
	// * @param compileError
	// * ��������G���[
	// * @return
	// */
	// private CDiagnostic getContainsSameError(List<CDiagnostic> compileErrors,
	// CDiagnostic compileError) {
	//
	// for (CDiagnostic error : compileErrors) {
	// if (isSameError(compileError, error)) {
	// return error;
	// }
	// }
	//
	// return null;
	// }
	//
	// /**
	// * 2�̃G���[�������G���[���𔻒f���܂�
	// *
	// * @param compileError1
	// * @param compileError2
	// * @return
	// */
	// private boolean isSameError(CDiagnostic compileError1,
	// CDiagnostic compileError2) {
	//
	// if (compileError1.getSymbol() != null) {
	// if (compileError1.getErrorMessage().equals(
	// compileError2.getErrorMessage())
	// && compileError1.getSymbol().equals(
	// compileError2.getSymbol())
	// && compileError1.getSymbolKind().equals(
	// compileError2.getSymbolKind())) {
	// return true;
	// }
	//
	// } else {
	// if (compileError1.getErrorMessage().equals(
	// compileError2.getErrorMessage())) {
	// return true;
	// }
	// }
	//
	// return false;
	// }
	//
	// /**
	// * 2�̃��X�g����C������Ă���G���[�̃��X�g���쐬���܂�
	// *
	// * @param prevErrors
	// * �O�̃R���p�C���|�C���g�̃G���[
	// * @param compileErrors
	// * ���݂̃R���p�C���|�C���g�̃G���[
	// * @return �C�����ꂽ�G���[�̃��X�g
	// */
	// private List<CDiagnostic> getCorrectionErrors(List<CDiagnostic>
	// prevErrors,
	// List<CDiagnostic> compileErrors) {
	//
	// List<CDiagnostic> correctionErrors = new ArrayList<CDiagnostic>();
	//
	// if (prevErrors == null || compileErrors == null) {
	// return correctionErrors;
	// }
	//
	// List<CDiagnostic> temp = new ArrayList<CDiagnostic>(compileErrors);
	// for (CDiagnostic compileError : prevErrors) {
	// if (getContainsSameError(temp, compileError) == null) {
	// correctionErrors.add(compileError);
	// } else {
	// removeSameError(temp, compileError);
	// }
	// }
	//
	// return correctionErrors;
	// }
	//
	// private void removeSameError(List<CDiagnostic> compileErrors,
	// CDiagnostic compileError) {
	// for (int i = 0; i < compileErrors.size(); i++) {
	// CDiagnostic err = compileErrors.get(i);
	// if (isSameError(err, compileError)) {
	// compileErrors.remove(i);
	// break;
	// }
	// }
	// }
	//
	// /**
	// * �Z�O�����g�̌W����ݒ肵�܂�
	// *
	// * @param histories
	// * @return
	// */
	// private void setA() {
	//
	// for (TCompileErrorHistory history : histories) {
	//
	// TCompileErrorHistorySegment endSegment = history.getSegments().get(
	// history.getSegments().size() - 1);
	//
	// // �G���[���C�����ꂸ���ʕ��Ɏc���Ă���ꍇ
	// if (endSegment.getEnd() == null) {
	// endSegment.setA(-1);
	// continue;
	// }
	//
	// List<CDiagnostic> correctionErrors = endSegment.getEnd()
	// .getCorrectionErrors();
	//
	// // �Ō�̃Z�O�����g�̌W����ݒ�
	// if (endSegment.getCompileError().getErrorMessage()
	// .equals("';' ������܂���B")) {
	// endSegment.setA(1 / (double) correctionErrors.size());
	// } else {
	// endSegment
	// .setA(1 / (double) getCorrectionErrorsCount(correctionErrors));
	// }
	//
	// int i;
	// for (i = history.getSegments().size() - 2; i >= 0; i--) {
	// TCompileErrorHistorySegment segment = history.getSegments()
	// .get(i);
	//
	// // �p�^�[��3-B�̏���
	// if (segment.getEnd().getCorrectionErrors().size() > 0) {
	// while (i >= 0) {
	// TCompileErrorHistorySegment seg = history.getSegments()
	// .get(i);
	// seg.setA(0);
	// i--;
	// }
	// break;
	// } else {
	//
	// // �W���̐ݒ�
	// if (correctionErrors.size() == 0) {
	// segment.setA(0);
	// } else {
	// if (segment.getCompileError().getErrorMessage()
	// .equals("';' ������܂���B")) {
	// segment.setA(1 / (double) correctionErrors.size());
	// } else {
	// segment.setA(1 / (double) getCorrectionErrorsCount(correctionErrors));
	// }
	// }
	//
	// // �Z�O�����g�ɂ�����C�����ꂽ�G���[�̃��X�g
	// List<CDiagnostic> tempList = new ArrayList<CDiagnostic>(
	// correctionErrors);
	// for (CDiagnostic correctionError : correctionErrors) {
	// if (getContainsSameError(segment.getEnd().getResult()
	// .getDiagnostics(), correctionError) == null) {
	// tempList.remove(correctionError);
	// }
	// }
	// correctionErrors = tempList;
	//
	// }
	//
	// }
	//
	// }
	// }
	//
	// private int getCorrectionErrorsCount(List<CDiagnostic> correctionErrors)
	// {
	// int num = correctionErrors.size();
	//
	// for (CDiagnostic compileError : correctionErrors) {
	// if (compileError.getErrorMessage().equals("';' ������܂���B")) {
	// num--;
	// }
	// }
	//
	// return num;
	// }
	//
	// private void setPattern() {
	//
	// TCompilePoint prevCompilePoint = null;
	// int pattern = -1; // -1:�R���p�C��1��ŏC������� 0:���̂܂� 1:������
	//
	// for (TCompilePoint compilePoint : compilePoints) {
	// List<CDiagnostic> compileErrors = compilePoint.getResult()
	// .getDiagnostics();
	// List<CDiagnostic> correctionErrors = compilePoint
	// .getCorrectionErrors();
	//
	// if (prevCompilePoint == null
	// || prevCompilePoint.getResult().getDiagnostics().size() == 0) {
	// compilePoint.setPattern(0);
	// } else {
	//
	// // �����܂܂��R���p�C���|�C���g
	// if (correctionErrors.size() > 0) {
	//
	// // �C���G���[����������
	// if (correctionErrors.size() > 1) {
	//
	// if (compileErrors.size() > 0) {
	//
	// switch (pattern) {
	// case -1:
	// case 0:
	// if (isPattern5(compilePoint, prevCompilePoint)) {
	// compilePoint.setPattern(7); // �p�^�[��5
	// } else {
	//
	// if (prevCompilePoint.getResult()
	// .getDiagnostics().size() < compileErrors
	// .size()
	// + compilePoint
	// .getCorrectionErrors()
	// .size()) {
	// // TODO �p�^�[��2-A�ƃp�^�[��3-A�̕���?
	// compilePoint.setPattern(8);
	// } else {
	// // �p�^�[��3-A
	// compilePoint.setPattern(4);
	// }
	// }
	// break;
	// case 1:
	// if (isPattern5(compilePoint, prevCompilePoint)) {
	// compilePoint.setPattern(7); // �p�^�[��5
	// } else {
	// compilePoint.setPattern(6); // �p�^�[��4
	//
	// // TODO �p�^�[��2-A�ƃp�^�[��4�̕���
	// }
	// break;
	// }
	//
	// } else {
	// switch (pattern) {
	// case -1:
	// case 0:
	// compilePoint.setPattern(4); // �p�^�[��3-A
	// break;
	// case 1:
	// compilePoint.setPattern(6); // �p�^�[��4
	// break;
	// }
	// }
	//
	// pattern = -1;
	//
	// } else {
	// switch (pattern) {
	// case -1:
	// int prevErrorCount = prevCompilePoint.getResult()
	// .getDiagnostics().size();
	// if (prevErrorCount == 0) {
	// if (compileErrors.size() == 0) {
	// // �p�^�[��1
	// compilePoint.setPattern(1);
	// } else {
	//
	// if (isPattern5(compilePoint,
	// prevCompilePoint)) {
	// // �p�^�[��5
	// compilePoint.setPattern(7);
	// } else {
	// // �p�^�[��2-A
	// compilePoint.setPattern(2);
	// }
	// }
	//
	// } else {
	//
	// if (prevErrorCount <= compileErrors.size()) {
	//
	// if (isPattern5(compilePoint,
	// prevCompilePoint)) {
	// // �p�^�[��5
	// compilePoint.setPattern(7);
	// } else {
	// // �p�^�[��2-A
	// compilePoint.setPattern(2);
	// }
	// } else if (hasCorrectionErrorsWithCompilePoints(
	// histories, correctionErrors.get(0))) {
	// // �p�^�[��3-B
	// compilePoint.setPattern(5);
	//
	// // TODO �p�^�[��2-A�ƃp�^�[��3-B�̕���
	//
	// } else {
	// // �p�^�[��1
	// compilePoint.setPattern(1);
	// }
	//
	// }
	// break;
	// case 0:
	// if (compileErrors.size() == 0) {
	// // �p�^�[��2-B
	// compilePoint.setPattern(3);
	//
	// } else {
	// if (hasCorrectionErrorsWithCompilePoints(
	// histories, correctionErrors.get(0))) {
	// // �p�^�[��3-B
	// compilePoint.setPattern(5);
	//
	// // TODO �p�^�[��2-A�ƃp�^�[��3-B�̕���
	//
	// } else {
	//
	// if (isPattern5(compilePoint,
	// prevCompilePoint)) {
	// // �p�^�[��5
	// compilePoint.setPattern(7);
	// } else {
	//
	// if (prevCompilePoint.getResult()
	// .getDiagnostics().size() < compileErrors
	// .size()
	// + correctionErrors.size()) {
	// // TODO �p�^�[��2-A��2-B�̕���
	// compilePoint.setPattern(2);
	// } else {
	// // �p�^�[��2-B
	// compilePoint.setPattern(3);
	// }
	//
	// }
	// }
	//
	// }
	// break;
	// case 1:
	// if (hasCorrectionErrorsWithCompilePoints(histories,
	// correctionErrors.get(0))) {
	// // �p�^�[��2-B
	// compilePoint.setPattern(3);
	// } else {
	// if (prevCompilePoint.getResult()
	// .getDiagnostics().size() <= compileErrors
	// .size()) {
	// // �p�^�[��2-A
	// compilePoint.setPattern(2);
	// } else {
	// // �p�^�[��1
	// compilePoint.setPattern(1);
	// }
	// }
	//
	// break;
	// }
	// }
	//
	// pattern = -1;
	//
	// } else {
	// if (compileErrors.size() > prevCompilePoint.getResult()
	// .getDiagnostics().size()) {
	// pattern = 1;
	// } else {
	// pattern = 0;
	// }
	// }
	//
	// }
	//
	// prevCompilePoint = compilePoint;
	// }
	// }
	//
	// private boolean hasCorrectionErrorsWithCompilePoints(
	// List<TCompileErrorHistory> histories, CDiagnostic compileError) {
	// for (TCompileErrorHistory history : histories) {
	// if (history.getEnd() != null
	// && history.getEnd().getCorrectionErrors()
	// .contains(compileError)) {
	// if (history.getSegments().size() > 1) {
	// for (TCompileErrorHistorySegment segment : history
	// .getSegments()) {
	// if (segment.getStart().getCorrectionErrors().size() > 0) {
	// return true;
	// }
	// }
	// }
	// }
	// }
	// return false;
	// }
	//
	// private boolean isPattern5(TCompilePoint compilePoint,
	// TCompilePoint prevCompilePoint) {
	// String errorKind = compilePoint.getResult().getDiagnostics().get(0)
	// .getMessageKind();
	// String prevErrorKind = prevCompilePoint.getResult().getDiagnostics()
	// .get(0).getMessageKind();
	//
	// if (prevErrorKind.equals("�Ӗ���̓G���[") && errorKind.equals("�\����̓G���[")) {
	// return true;
	// }
	//
	// return false;
	// }
	//
	// /****************************************************
	// * CompileLogs
	// ****************************************************/
	//
	// /**
	// * �����file�̃R���p�C�����O��Ԃ��܂�
	// *
	// * @param file
	// * @return
	// */
	// private CTimeOrderedList<PLLog> getCompileLogs(PLFile file) {
	// CTimeOrderedList<PLLog> logs = file.getOrderedLogs();
	// CTimeOrderedList<PLLog> compileLogs = logs
	// .select(new ICChecker<PLLog>() {
	// @Override
	// public boolean check(PLLog t) {
	// return t.getSubType().equals("COMPILE");
	// }
	// });
	// return compileLogs;
	// }

}
