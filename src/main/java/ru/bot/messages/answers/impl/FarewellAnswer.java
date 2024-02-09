package ru.bot.messages.answers.impl;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.bot.db.RedisHelper;
import ru.bot.messages.answers.IAnswerMessages;

public class FarewellAnswer implements IAnswerMessages {
	private static final String ANSWER = RedisHelper.getString("farewellAnswers");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String findAnswer(String text, Message message) {
		if (message.getLeftChatMember() == null) {
			return null;
		}

		return ANSWER;
	}

}
