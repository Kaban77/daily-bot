package ru.bot.messages.tasks;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimerTask;

import ru.bot.messages.stats.RandomUserStatsRepository;

public class ClearStatsTask extends TimerTask {

	@Override
	public void run() {
		if (!canStartNow()) {
			return;
		}

		RandomUserStatsRepository.clearMap();
	}

	private boolean canStartNow() {
		var now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

		return now.getDayOfYear() == 1 && now.getHour() < 12;
	}

}
