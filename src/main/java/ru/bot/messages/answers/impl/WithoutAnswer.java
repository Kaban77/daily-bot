package ru.bot.messages.answers.impl;

import org.apache.commons.lang3.StringUtils;

import ru.bot.db.RedisHelper;
import ru.bot.messages.answers.AnswerTextMessages;

public class WithoutAnswer extends AnswerTextMessages {

	private static final String MESSAGE = RedisHelper.getString("withoutMessage");
	private static final String ANSWER = RedisHelper.getString("noAnswer");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String findAnswer(String message, Long userId) {
		if (StringUtils.equalsIgnoreCase(message, MESSAGE)) {
			return ANSWER;
		}
		return null;
	}
}
