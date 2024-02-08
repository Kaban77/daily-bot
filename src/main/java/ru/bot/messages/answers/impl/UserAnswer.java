package ru.bot.messages.answers.impl;

import java.util.random.RandomGeneratorFactory;

import ru.bot.db.RedisHelper;
import ru.bot.messages.answers.AnswerTextMessages;

public class UserAnswer extends AnswerTextMessages {

	private static final int PROBABILITY = 3;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String findAnswer(String message, Long userId) {
		var random = RandomGeneratorFactory.all()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Random wasn't found"))
				.create();
		int value = random.nextInt(100);

		if (value > PROBABILITY) {
			return null;
		}

		var mapAnswers = RedisHelper.getMap("userAnswer");
		return mapAnswers.get(userId.toString());
	}

}
