package ru.bot.messages.answers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

import org.telegram.telegrambots.meta.api.objects.Message;

public class AnswersHandler {

	private static final List<IAnswerMessages> IMPLS = new ArrayList<>();

	static {
		ServiceLoader.load(IAnswerMessages.class).forEach(IMPLS::add);
	}

	public static String getAnswer(String text, Message message) {
		return IMPLS.stream()
				.map(i -> i.findAnswer(text, message))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}
}
