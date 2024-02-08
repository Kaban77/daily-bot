package ru.bot.messages.answers;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface IAnswerMessages {

	/**
	 * It finds an answer for incoming message
	 * 
	 * @param text
	 *            incoming text (nullable)
	 * @param message
	 *            incoming message (not null)
	 * @return answer (nullable)
	 */
	String findAnswer(String text, Message message);

}
