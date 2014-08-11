package generef.knowledge;

import java.util.ArrayList;
import java.util.List;

public class RSFailureKnowledgeRepository {

	private List<RSFKWritingPoint> points = new ArrayList<RSFKWritingPoint>();

	public void addAll(List<RSFailureKnowledge> failureKnowledges) {
		points.add(new RSFKWritingPoint(failureKnowledges));
	}

	public List<RSFailureKnowledge> getFailureKnowledges() {
		List<RSFailureKnowledge> failureKnowledges = new ArrayList<RSFailureKnowledge>();
		for (RSFKWritingPoint point : points) {
			failureKnowledges.addAll(point.getKnowledgeList());
		}
		return failureKnowledges;
	}

	public List<RSFKWritingPoint> getPoints() {
		return points;
	}

	/**
	 * message�Ŏw�肳�ꂽ�G���[���b�Z�[�W�̎��s�m�������X�g����S�Ď��o���܂�
	 * 
	 * @param message
	 *            �G���[���b�Z�[�W
	 * @return
	 */
	public List<RSFailureKnowledge> getFailureKnowledges(String message) {
		ArrayList<RSFailureKnowledge> list = new ArrayList<RSFailureKnowledge>();

		for (RSFailureKnowledge knowledge : getFailureKnowledges()) {
			if (knowledge.getCompileError().getMessageParser()
					.getAbstractionMessage().equals(message)) {
				list.add(knowledge);
			}
		}

		return list;
	}

	/**
	 * ���s�m�����X�g����G���[���b�Z�[�W�̎�ނ����o���܂�
	 * 
	 * @return String�^�̃G���[���b�Z�[�W���X�g
	 */
	public List<String> getFailureKnowledgeKinds() {
		ArrayList<String> list = new ArrayList<String>();

		for (RSFailureKnowledge knowledge : getFailureKnowledges()) {
			String message = knowledge.getCompileError().getMessageParser()
					.getErrorMessage();
			if (!list.contains(message)) {
				list.add(message);
			}
		}

		return list;
	}
}
