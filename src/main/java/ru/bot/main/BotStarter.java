package ru.bot.main;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bot.locks.AbstractStartCallback;
import ru.bot.locks.BotLock;
import ru.bot.locks.LockCheckerTask;

public class BotStarter {
	private static final Logger LOGGER = LoggerFactory.getLogger(BotStarter.class);

	public static void start(String processId, AbstractStartCallback startCallback) {
		Runtime.getRuntime()
				.addShutdownHook(new Thread(() -> startCallback.stop(processId, BotLock::unlock), "unlock"));

		var timer = new Timer("lock-checker", false);
		timer.scheduleAtFixedRate(new LockCheckerTask(processId, startCallback), 0L, 5 * 1000L);

		LOGGER.info("App is started");
	}
}
