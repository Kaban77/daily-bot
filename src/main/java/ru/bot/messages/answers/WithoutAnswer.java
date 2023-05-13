package ru.bot.messages.answers;

import org.apache.commons.lang3.StringUtils;

import ru.bot.db.RedisHelper;

public class WithoutAnswer implements IAnswerMessages {

	private static final String MESSAGE = RedisHelper.INSTANCE.getString("withoutMessage");
	private static final String ANSWER = RedisHelper.INSTANCE.getString("noAnswer");

	@Override
	public String findAnswer(String message) {
		if (StringUtils.equalsIgnoreCase(message, MESSAGE)) {
			return ANSWER;
		}
		return null;
	}
}
