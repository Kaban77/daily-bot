package ru.bot.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import ru.bot.errors.BotErrorException;
import ru.bot.messages.polling.DailyLongPollingBot;
import ru.bot.messages.tasks.Send4Task;
import ru.bot.messages.tasks.SendGoodMorningMessageTask;
import ru.bot.messages.tasks.SendRandomAudioTask;
import ru.bot.messages.tasks.SendRandomUserMessage;

public class DailyBotStartCallback extends AbstractStartCallback {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBotStartCallback.class);

	private BotSession dailyBotSession;
	private final List<Timer> timers = new ArrayList<>();

	@Override
	public synchronized void doStart() {
		try {
			var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			var dailyBot = new DailyLongPollingBot();

			dailyBotSession = telegramBotsApi.registerBot(dailyBot);

			submitTask(new Send4Task(dailyBot), 30 * 1000L);
			submitTask(new SendGoodMorningMessageTask(dailyBot), 30 * 1000L);
			submitTask(new SendRandomUserMessage(dailyBot), 30 * 1000L);
			submitTask(new SendRandomAudioTask(dailyBot), 30 * 1000L);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw BotErrorException.valueOf(e);
		}

	}

	@Override
	public synchronized void doStop() {
		if (dailyBotSession != null) {
			dailyBotSession.stop();
		}

		timers.forEach(Timer::cancel);
		timers.clear();
	}

	private void submitTask(TimerTask timerTask, long period) {
		var timer = new Timer("message-senders-" + timerTask.getClass(), true);
		timer.scheduleAtFixedRate(timerTask, 0L, period);

		timers.add(timer);
	}

}
