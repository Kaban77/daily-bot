package ru.bot.main;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bot.locks.AbstractStartCallback;
import ru.bot.locks.BotLock;

public class BotStarter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BotStarter.class);

	public static void start(String processId, AbstractStartCallback startCallback) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> startCallback.stop(processId, BotLock::unlock)));

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()-> {
			if (!startCallback.isStarted()) {
				if (!BotLock.tryLock(processId)) {
					return;
				}

				LOGGER.info("Lock aquired");

				try {
					startCallback.start();
				} catch (Exception e) {
					LOGGER.error("Failed to start bot :(", e);
					startCallback.stop(processId, BotLock::unlock);
				}
			} else {
				if (!BotLock.isLocked(processId)) {
					LOGGER.info("Lock lost. Bot is stopping");
					startCallback.stop(processId, BotLock::unlock);
				}
			}
		}, 0, 5 * 1000L, TimeUnit.MILLISECONDS);

		LOGGER.info("App started");
	}
}
