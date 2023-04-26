package ru.bot.locks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStartCallback {

	private final AtomicBoolean started = new AtomicBoolean(false);
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStartCallback.class);

	public abstract void doStart();

	public abstract void doStop();

	public boolean isStarted() {
		return started.get();
	}

	public final void start() {
		started.set(true);
		try {
			doStart();
		} catch (Exception e) {
			started.set(false);
			throw e;
		}
	}

	public final void stop(String processId, Consumer<String> afterDoStopConsumer) {
		if (!started.getAndSet(false)) {
			return;
		}

		try {
			doStop();
		} catch (Exception e) {
			LOGGER.error("Failed to stop callback", e);
		}

		afterDoStopConsumer.accept(processId);
	}

}
