package ru.bot.messages.answers.impl;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.bot.db.RedisHelper;
import ru.bot.messages.answers.IAnswerMessages;

public class WelcomeAnswer implements IAnswerMessages {
	private static final String ANSWER = RedisHelper.getString("welcomeAnswer");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String findAnswer(String text, Message message) {
		if (message.getNewChatMembers().isEmpty()) {
			return null;
		}

		return ANSWER;
	}
}
