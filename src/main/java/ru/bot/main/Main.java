package ru.bot.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import ru.bot.errors.BotErrorException;
import ru.bot.polling.DailyLongPollingBot;
import ru.bot.tasks.Send4Task;
import ru.bot.tasks.SendGoodMorningMessageTask;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final ScheduledExecutorService SCHEDULER_EXECUTOR = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		try {
			var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			var dailyBot = new DailyLongPollingBot();

			telegramBotsApi.registerBot(dailyBot);

			SCHEDULER_EXECUTOR.scheduleAtFixedRate(new Send4Task(dailyBot), 30, 30, TimeUnit.SECONDS);
			SCHEDULER_EXECUTOR.scheduleAtFixedRate(new SendGoodMorningMessageTask(dailyBot), 30, 30, TimeUnit.SECONDS);

			LOGGER.info("App was started");
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw BotErrorException.valueOf(e);
		}
	}

}
