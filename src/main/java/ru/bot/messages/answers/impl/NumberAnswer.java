package ru.bot.messages.answers.impl;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import ru.bot.db.RedisHelper;
import ru.bot.messages.answers.IAnswerMessages;

public class NumberAnswer implements IAnswerMessages {

	private static final Collection<String> MESSAGES = RedisHelper.INSTANCE.getCollection("numberMessages");
	private static final String ANSWER = RedisHelper.INSTANCE.getString("numberAnswer");

	@Override
	public String findAnswer(String message, Long userId) {
		if (MESSAGES.stream()
				.filter(m -> StringUtils.containsIgnoreCase(message, m))
				.findFirst()
				.orElse(null) != null) {
			return ANSWER;
		}

		return null;
	}
}
