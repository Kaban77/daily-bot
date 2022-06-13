package ru.bot.tasks;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import ru.bot.db.DBHelper;
import ru.bot.errors.BotErrorException;
import ru.bot.errors.BotErrors;

public abstract class AbstractSendTask implements Runnable {

	private final String[] startTime;
	private long delay = System.currentTimeMillis();

	public AbstractSendTask(String startTimeDbKey) {
		var startTimeString = DBHelper.INSTANCE.getString(startTimeDbKey);

		if (StringUtils.isBlank(startTimeString)) {
			throw new BotErrorException("Not found value in DB: " + startTimeDbKey, BotErrors.NO_DATA_FOUND_IN_DB)
					.withParams(startTimeDbKey);
		}

		startTime = startTimeString.split(",");
	}

	protected boolean canStartNow() {
		if (delay > System.currentTimeMillis()) {
			return false;
		}

		var now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

		for (var time : startTime) {
			var hourMinute = time.split(":");

			if (Integer.parseInt(hourMinute[0].trim()) == now.getHour() && Integer.parseInt(hourMinute[1].trim()) == now.getMinute()) {
				delay = System.currentTimeMillis() + DateUtils.MILLIS_PER_MINUTE;
				return true;
			}
		}

		return false;
	}
}
