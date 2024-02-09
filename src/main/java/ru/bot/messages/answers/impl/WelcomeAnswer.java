package ru.bot.messages.answers.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.bot.db.RedisHelper;
import ru.bot.messages.answers.IAnswerMessages;
import ru.bot.random.RandomHelper;

public class WelcomeAnswer implements IAnswerMessages {
	private static final Collection<String> ANSWERS = RedisHelper.getCollection("welcomeAnswers");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String findAnswer(String text, Message message) {
		if (message.getNewChatMembers().isEmpty()) {
			return null;
		}
		
		var random = RandomHelper.getRandom();

		return new ArrayList<>(ANSWERS).get(random.nextInt(ANSWERS.size()));
	}
}
