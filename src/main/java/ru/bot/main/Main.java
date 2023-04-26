package ru.bot.main;

import java.util.UUID;

import ru.bot.locks.DailyBotStartCallback;

public class Main {

	public static void main(String[] args) {
		var processId = UUID.randomUUID().toString();
		BotStarter.start(processId, new DailyBotStartCallback());
	}

}
