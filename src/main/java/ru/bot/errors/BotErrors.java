package ru.bot.errors;

public enum BotErrors {

	FAILED_START("Ошибка при запуске приложения"),

	CONFIG_FILE_NOT_FOUND("Не найден конфиг файл"),

	NO_DATA_FOUND_IN_DB("Отсутствуют данные в БД. Ключ: %s"),

	DEFAULT("Что-то пошло не так :(");

	private final String errorText;

	private BotErrors(String errorText) {
		this.errorText = errorText;
	}

	public String getErrorText() {
		return errorText;
	}

}
