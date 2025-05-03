package ru.bot.messages.tasks;

import java.util.concurrent.Callable;

import javax.validation.constraints.Positive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bot.errors.BotErrorException;

public class CallableWithRetry {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallableWithRetry.class);

	private final int maxAttempts;

	public CallableWithRetry(@Positive int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public <T> T callWithRetry(Callable<T> sender) {
		int i = 0;
		T result;

		while (true) {
			try {
				result = sender.call();
				break;
			} catch (Exception e) {
				i++;
				LOGGER.error("Failed to send message", e);
				if (i >= maxAttempts) {
					throw BotErrorException.valueOf(e);
				}
			}
		}

		return result;
	}
}
