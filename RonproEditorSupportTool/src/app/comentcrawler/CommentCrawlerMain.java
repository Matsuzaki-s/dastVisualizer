package app.comentcrawler;

import framework.DnDFramework;

public class CommentCrawlerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DnDFramework.open("�R�����g���W", "�t�H���_���h���b�v���Ă�������",
				new CommentCrawleStrategy());
	}

}
