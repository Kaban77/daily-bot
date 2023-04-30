package ru.bot.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import ru.bot.errors.BotErrorException;
import ru.bot.polling.DailyLongPollingBot;
import ru.bot.tasks.Send4Task;
import ru.bot.tasks.SendGoodMorningMessageTask;

public class DailyBotStartCallback extends AbstractStartCallback {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBotStartCallback.class);

	private BotSession dailyBotSession;
	private final List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();

	@Override
	public void doStart() {
		try {
			var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			var dailyBot = new DailyLongPollingBot();

			dailyBotSession = telegramBotsApi.registerBot(dailyBot);

			scheduledFutures.add(Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Send4Task(dailyBot), 0, 30, TimeUnit.SECONDS));
			scheduledFutures.add(Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new SendGoodMorningMessageTask(dailyBot), 0, 30,
					TimeUnit.SECONDS));
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw BotErrorException.valueOf(e);
		}

	}

	@Override
	public void doStop() {
		if (dailyBotSession != null) {
			dailyBotSession.stop();
		}

		scheduledFutures.forEach(sf -> sf.cancel(false));
		scheduledFutures.clear();
	}

}
