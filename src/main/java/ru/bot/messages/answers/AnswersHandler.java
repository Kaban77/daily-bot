package ru.bot.messages.answers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;

public class AnswersHandler {

	private static final List<IAnswerMessages> IMPLS = new ArrayList<>();

	static {
		ServiceLoader.load(IAnswerMessages.class).forEach(IMPLS::add);
	}

	public static String getAnswer(String message) {
		var filteredMessage = message
				.replaceAll("\\(", StringUtils.EMPTY)
				.replaceAll("\\)", StringUtils.EMPTY)
				.replaceAll("\\.", StringUtils.EMPTY)
				.replaceAll("\\,", StringUtils.EMPTY)
				.trim();

		return IMPLS.stream()
				.map(i -> i.findAnswer(filteredMessage))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}
}
