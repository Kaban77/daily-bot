package ru.bot.locks;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bot.errors.BotErrorException;

public class LockCheckerTask extends TimerTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(LockCheckerTask.class);
	private static final AtomicInteger ERROR_COUNT = new AtomicInteger();

	private final String processId;
	private final AbstractStartCallback startCallback;

	public LockCheckerTask(String processId, AbstractStartCallback startCallback) {
		this.processId = processId;
		this.startCallback = startCallback;
	}

	@Override
	public void run() {
		try {
			if (!startCallback.isStarted()) {
				if (!BotLock.tryLock(processId)) {
					return;
				}

				LOGGER.info("Lock aquired");

				try {
					startCallback.start();
					ERROR_COUNT.set(0);
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
		} catch (BotErrorException e) {
			LOGGER.info("App is stoped");
			LOGGER.error(e.getLocalizedMessage(), e);
			Runtime.getRuntime().exit(1);
		} catch (Exception e) {
			LOGGER.error("Unknown error!", e);
			if (ERROR_COUNT.incrementAndGet() > 40) {
				LOGGER.info("App is stoped");
				Runtime.getRuntime().exit(1);
			}
		}

	}

}
