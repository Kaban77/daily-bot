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

import redis.clients.jedis.exceptions.JedisConnectionException;
import ru.bot.errors.BotErrorException;
import ru.bot.messages.polling.DailyLongPollingBot;
import ru.bot.messages.tasks.CallableWithRetry;
import ru.bot.messages.tasks.ClearStatsTask;
import ru.bot.messages.tasks.Send4Task;
import ru.bot.messages.tasks.SendGoodMorningMessageTask;
import ru.bot.messages.tasks.SendRandomAudioTask;
import ru.bot.messages.tasks.SendRandomUserMessage;

public class DailyBotStartCallback extends AbstractStartCallback {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBotStartCallback.class);
	private static final int MAX_ATTEMPTS = 2;

	private BotSession dailyBotSession;
	private final List<Timer> timers = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 * @throws JedisConnectionException It can't connect to redis
	 * @throws BotErrorException other errors
	 */
	@Override
	public void doStart() {
		try {
			var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			var dailyBot = new DailyLongPollingBot();

			dailyBotSession = telegramBotsApi.registerBot(dailyBot);

			var callableWithRetry = new CallableWithRetry(MAX_ATTEMPTS);

			submitTask(new Send4Task(dailyBot), 30 * 1000L);
			submitTask(new SendGoodMorningMessageTask(dailyBot), 30 * 1000L);
			submitTask(new SendRandomUserMessage(dailyBot, callableWithRetry), 30 * 1000L);
			submitTask(new SendRandomAudioTask(dailyBot), 30 * 1000L);
			submitTask(new ClearStatsTask(callableWithRetry), 3600000L);
		} catch (JedisConnectionException ste) {
			throw ste;
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

		timers.forEach(Timer::cancel);
		timers.clear();
	}

	private void submitTask(TimerTask timerTask, long period) {
		var timer = new Timer("message-senders-" + timerTask.getClass(), true);
		timer.scheduleAtFixedRate(timerTask, 0L, period);

		timers.add(timer);
	}

}
