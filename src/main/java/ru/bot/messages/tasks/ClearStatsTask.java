package ru.bot.messages.tasks;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimerTask;

import ru.bot.messages.stats.RandomUserStatsRepository;

public class ClearStatsTask extends TimerTask {

	private final CallableWithRetry callableWithRetry;

	public ClearStatsTask(CallableWithRetry callableWithRetry) {
		this.callableWithRetry = callableWithRetry;
	}

	@Override
	public void run() {
		if (!canStartNow()) {
			return;
		}

		callableWithRetry.callWithRetry(() -> {
			RandomUserStatsRepository.clearMap();
			return null;
		});
	}

	private boolean canStartNow() {
		var now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

		return now.getDayOfYear() == 1 && now.getHour() < 12;
	}

}
