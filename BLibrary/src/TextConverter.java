

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * TextConverter
 */
public class TextConverter {

	private static final Map<String, String> jptable = new LinkedHashMap<String, String>();

	static {

		jptable.put("ttsa", "����");
		jptable.put("ttsi", "����");
		jptable.put("ttsu", "����");
		jptable.put("ttse", "����");
		jptable.put("ttso", "����");

		jptable.put("ssha", "������");
		jptable.put("sshi", "����");
		jptable.put("sshu", "������");
		jptable.put("sshe", "������");
		jptable.put("ssho", "������");
		jptable.put("ssya", "������");
		jptable.put("ssyi", "������");
		jptable.put("ssyu", "������");
		jptable.put("ssye", "������");
		jptable.put("ssyo", "������");

		jptable.put("ccha", "������");
		jptable.put("cchi", "����");
		jptable.put("cchu", "������");
		jptable.put("cche", "������");
		jptable.put("ccho", "������");
		jptable.put("ttya", "������");
		jptable.put("ttyi", "������");
		jptable.put("ttyu", "������");
		jptable.put("ttye", "������");
		jptable.put("ttyo", "������");

		jptable.put("kka", "����");
		jptable.put("kki", "����");
		jptable.put("kku", "����");
		jptable.put("kke", "����");
		jptable.put("kko", "����");
		jptable.put("ssa", "����");
		jptable.put("ssi", "����");
		jptable.put("ssu", "����");
		jptable.put("sse", "����");
		jptable.put("sso", "����");
		jptable.put("tta", "����");
		jptable.put("tti", "����");
		jptable.put("ttu", "����");
		jptable.put("tte", "����");
		jptable.put("tto", "����");
		jptable.put("hha", "����");
		jptable.put("hhi", "����");
		jptable.put("hhu", "����");
		jptable.put("hhe", "����");
		jptable.put("hho", "����");
		jptable.put("ppa", "����");
		jptable.put("ppi", "����");
		jptable.put("ppu", "����");
		jptable.put("ppe", "����");
		jptable.put("ppo", "����");

		jptable.put("tha", "�Ă�");
		jptable.put("thi", "�Ă�");
		jptable.put("thu", "�Ă�");
		jptable.put("the", "�Ă�");
		jptable.put("tho", "�Ă�");
		jptable.put("tsa", "��");
		jptable.put("tsi", "��");
		jptable.put("tsu", "��");
		jptable.put("tse", "��");
		jptable.put("tso", "��");
		jptable.put("jya", "����");
		jptable.put("jyi", "����");
		jptable.put("jyu", "����");
		jptable.put("jye", "����");
		jptable.put("jyo", "����");
		jptable.put("bya", "�т�");
		jptable.put("byi", "�т�");
		jptable.put("byu", "�т�");
		jptable.put("bye", "�т�");
		jptable.put("byo", "�т�");
		jptable.put("sya", "����");
		jptable.put("syi", "����");
		jptable.put("syu", "����");
		jptable.put("sye", "����");
		jptable.put("syo", "����");
		jptable.put("sha", "����");
		jptable.put("shi", "��");
		jptable.put("shu", "����");
		jptable.put("she", "����");
		jptable.put("sho", "����");
		jptable.put("mya", "�݂�");
		jptable.put("myi", "�݂�");
		jptable.put("myu", "�݂�");
		jptable.put("mye", "�݂�");
		jptable.put("myo", "�݂�");
		jptable.put("rya", "���");
		jptable.put("ryi", "�股");
		jptable.put("ryu", "���");
		jptable.put("rye", "�肥");
		jptable.put("ryo", "���");
		jptable.put("kya", "����");
		jptable.put("kyi", "����");
		jptable.put("kyu", "����");
		jptable.put("kye", "����");
		jptable.put("kyo", "����");
		jptable.put("gya", "����");
		jptable.put("gyi", "����");
		jptable.put("gyu", "����");
		jptable.put("gye", "����");
		jptable.put("gyo", "����");
		jptable.put("tya", "����");
		jptable.put("tyi", "����");
		jptable.put("tyu", "����");
		jptable.put("tye", "����");
		jptable.put("tyo", "����");
		jptable.put("nya", "�ɂ�");
		jptable.put("nyi", "�ɂ�");
		jptable.put("nyu", "�ɂ�");
		jptable.put("nye", "�ɂ�");
		jptable.put("nyo", "�ɂ�");
		jptable.put("hya", "�Ђ�");
		jptable.put("hyi", "�Ђ�");
		jptable.put("hyu", "�Ђ�");
		jptable.put("hye", "�Ђ�");
		jptable.put("hyo", "�Ђ�");
		jptable.put("ja", "����");
		jptable.put("ji", "��");
		jptable.put("ju", "����");
		jptable.put("je", "����");
		jptable.put("jo", "����");
		jptable.put("cha", "����");
		jptable.put("chi", "��");
		jptable.put("chu", "����");
		jptable.put("che", "����");
		jptable.put("cho", "����");
		jptable.put("fa", "�ӂ�");
		jptable.put("fi", "�ӂ�");
		jptable.put("fu", "��");
		jptable.put("fe", "�ӂ�");
		jptable.put("fo", "�ӂ�");

		jptable.put("xka", "��");
		jptable.put("xke", "��");
		jptable.put("xtu", "��");
		jptable.put("xna", "��");
		jptable.put("xni", "��");
		jptable.put("xnu", "��");
		jptable.put("xne", "��");
		jptable.put("xno", "��");
		jptable.put("xya", "��");
		jptable.put("xyu", "��");
		jptable.put("xyo", "��");
		jptable.put("xwa", "��");
		jptable.put("xn", "��");
		jptable.put("xa", "��");
		jptable.put("xi", "��");
		jptable.put("xu", "��");
		jptable.put("xe", "��");
		jptable.put("xo", "��");

		jptable.put("ka", "��");
		jptable.put("ki", "��");
		jptable.put("ku", "��");
		jptable.put("ke", "��");
		jptable.put("ko", "��");
		jptable.put("sa", "��");
		jptable.put("si", "��");
		jptable.put("su", "��");
		jptable.put("se", "��");
		jptable.put("so", "��");
		jptable.put("ta", "��");
		jptable.put("ti", "��");
		jptable.put("tu", "��");
		jptable.put("te", "��");
		jptable.put("to", "��");
		jptable.put("na", "��");
		jptable.put("ni", "��");
		jptable.put("nu", "��");
		jptable.put("ne", "��");
		jptable.put("no", "��");
		jptable.put("ha", "��");
		jptable.put("hi", "��");
		jptable.put("hu", "��");
		jptable.put("he", "��");
		jptable.put("ho", "��");
		jptable.put("ma", "��");
		jptable.put("mi", "��");
		jptable.put("mu", "��");
		jptable.put("me", "��");
		jptable.put("mo", "��");
		jptable.put("ya", "��");
		jptable.put("yi", "��");
		jptable.put("yu", "��");
		jptable.put("ye", "����");
		jptable.put("yo", "��");
		jptable.put("ra", "��");
		jptable.put("ri", "��");
		jptable.put("ru", "��");
		jptable.put("re", "��");
		jptable.put("ro", "��");
		jptable.put("wa", "��");
		jptable.put("wi", "����");
		jptable.put("wu", "��");
		jptable.put("we", "����");
		jptable.put("wo", "��");

		jptable.put("nn", "��");

		jptable.put("ga", "��");
		jptable.put("gi", "��");
		jptable.put("gu", "��");
		jptable.put("ge", "��");
		jptable.put("go", "��");
		jptable.put("za", "��");
		jptable.put("zi", "��");
		jptable.put("zu", "��");
		jptable.put("ze", "��");
		jptable.put("zo", "��");
		jptable.put("da", "��");
		jptable.put("di", "��");
		jptable.put("du", "��");
		jptable.put("de", "��");
		jptable.put("do", "��");
		jptable.put("ba", "��");
		jptable.put("bi", "��");
		jptable.put("bu", "��");
		jptable.put("be", "��");
		jptable.put("bo", "��");
		jptable.put("pa", "��");
		jptable.put("pi", "��");
		jptable.put("pu", "��");
		jptable.put("pe", "��");
		jptable.put("po", "��");
		jptable.put("la", "��");
		jptable.put("li", "��");
		jptable.put("lu", "��");
		jptable.put("le", "��");
		jptable.put("lo", "��");

		jptable.put("a", "��");
		jptable.put("i", "��");
		jptable.put("u", "��");
		jptable.put("e", "��");
		jptable.put("o", "��");

		jptable.put("�C", "�A");
		jptable.put(",", "�A");
		jptable.put("�D", "�B");
		jptable.put(".", "�B");
	}

	public String convert(String input) {
		Set<String> keys = jptable.keySet();
		for (String key : keys) {
			int index = input.indexOf(key);
			if (index != -1) {
				return input.substring(0, index) + jptable.get(key);
			}
		}
		return input;
	}
}
