/*
 * Created on 2007/06/13
 *
 * Copyright (c) 2007 camei.  All rights reserved.
 */
package obpro.plugin.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import obpro.plugin.ObproPlugin;

/**
 * �e���v���[�g�W�F�l���[�^ Java�̃N���X�쐬�ɓ������Ă܂��B
 * 
 * @author camei
 * @version $Id: Template.java,v 1.2 2009/05/08 09:20:50 macchan Exp $
 */
public class Template {

	// ���J�萔
	public static final String LINE_DELIMITER = System
			.getProperty("line.separator");

	// �萔
	private static Pattern variablePattern = Pattern
			.compile("\\$\\{([^\\}]*)\\}");

	// ����
	private Map<String, String> variables = new HashMap<String, String>();

	// : String>

	public String generate(URL url) {
		try {
			InputStream inputStream = url.openStream();
			return generate(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String generate(InputStream inputStream) {
		StringWriter os = new StringWriter();
		generate(os, inputStream);
		return os.toString();
	}

	/**
	 * @param reader
	 * @param os
	 */
	public void generate(Writer os, InputStream inputStream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, ObproPlugin.TEMPLATE_ENCODING));
			BufferedWriter writer = new BufferedWriter(os);

			String line;
			while ((line = reader.readLine()) != null) {
				// �ϐ�������u������
				String replaced = replaceVariable(line);

				// �u�����ʂ��o��
				writer.write(replaced);
				writer.newLine();
			}
			reader.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param line
	 * @return
	 */
	private String replaceVariable(String line) {
		// �ϐ��\���Ƀ}�b�`������
		Matcher matcher = variablePattern.matcher(line);

		// �}�b�`����������u��
		StringBuffer replaced = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String variable = getVariable(key);
			if (variable == null) {
				variable = "unknown";
			}
			matcher.appendReplacement(replaced, variable);
		}
		matcher.appendTail(replaced);
		return replaced.toString();
	}

	/**********
	 * �ϐ��ݒ�
	 **********/

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setVariable(String key, String value) {
		variables.put(key, value);
	}

	public String getVariable(String key) {
		return (String) variables.get(key);
	}

	public boolean containsKey(String key) {
		return variables.containsKey(key);
	}

	/*********
	 * utility
	 *********/

	/**
	 * �N���X����ϐ����ɕϊ�
	 * 
	 * @param className
	 *            �N���X��
	 * @return �N���X����java�K��Ɋ�Â��ϐ����ɂ���������
	 */
	public static String toInstanceName(String className) {
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}

	public static String createImportText(String[] classPaths) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < classPaths.length; i++) {
			buf.append("import " + classPaths[i] + ";" + LINE_DELIMITER);
		}
		return buf.toString();

	}

	/*********
	 * test
	 *********/

	public static void main(String[] args) {
		new Template().test();
	}

	private void test() {
		String templateData = "�l��${name}�ł��B\n�N���${age}�΂ł��B";

		setVariable("name", "���Y\n���s����");
		setVariable("age", "31");

		System.out.println(replaceVariable(templateData));
	}
}
