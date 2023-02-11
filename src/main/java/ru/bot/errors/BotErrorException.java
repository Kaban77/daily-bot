package ru.bot.errors;

import java.io.Serial;

public class BotErrorException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1602279301471001135L;

	private final BotErrors error;
	private transient Object[] params;

	public BotErrorException(String message) {
		this(message, BotErrors.DEFAULT);
	}

	public BotErrorException(String message, BotErrors error) {
		this(message, error, null);
	}

	public BotErrorException(String message, BotErrors error, Throwable e) {
		super(message, e);

		this.error = error;
	}

	public BotErrors getError() {
		return error;
	}

	public BotErrorException withParams(Object... params) {
		this.params = params;
		return this;
	}

	@Override
	public String getLocalizedMessage() {
		return String.format(error.getErrorText(), params);
	}

	public static BotErrorException valueOf(Throwable e) {
		BotErrors botError;

		if (e instanceof BotErrorException) {
			var tmp = (BotErrorException) e;
			botError = tmp.getError();
		} else {
			botError = BotErrors.DEFAULT;
		}

		return new BotErrorException(e.getMessage(), botError, e);
	}

}
