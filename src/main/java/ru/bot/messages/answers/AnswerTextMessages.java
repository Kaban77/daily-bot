package ru.bot.messages.answers;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class AnswerTextMessages implements IAnswerMessages {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String findAnswer(String text, Message message) {
		if (StringUtils.isBlank(text)) {
			return null;
		}
		
		var filteredText = text
				.replaceAll("\\(", StringUtils.EMPTY)
				.replaceAll("\\)", StringUtils.EMPTY)
				.replaceAll("\\.", StringUtils.EMPTY)
				.replaceAll("\\,", StringUtils.EMPTY)
				.trim();
		
		return findAnswer(filteredText, message.getFrom().getId());
	}

	/**
	 * It finds an answer for incoming message
	 * 
	 * @param text
	 *            incoming text (not null)
	 * @param userId
	 *            TG user ID
	 * @return answer (nullable)
	 */
	protected abstract String findAnswer(String text, Long userId);
}
