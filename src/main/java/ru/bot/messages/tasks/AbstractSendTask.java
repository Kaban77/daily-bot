package ru.bot.messages.tasks;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import ru.bot.db.RedisHelper;
import ru.bot.errors.BotErrorException;
import ru.bot.errors.BotErrors;

public abstract class AbstractSendTask extends TimerTask {

	private final String[] startTime;

	public AbstractSendTask(String startTimeDbKey) {
		var startTimeString = RedisHelper.getString(startTimeDbKey);

		if (StringUtils.isBlank(startTimeString)) {
			throw new BotErrorException("Not found value in DB: " + startTimeDbKey, BotErrors.NO_DATA_FOUND_IN_DB)
					.withParams(startTimeDbKey);
		}

		startTime = startTimeString.split(",");
	}

	protected boolean canStartNow() {
		var delayString = RedisHelper.getString(getTaskName());
		long delay;
		if (StringUtils.isNotBlank(delayString)) {
			delay = Long.parseLong(delayString);
		} else {
			delay = System.currentTimeMillis();
		}
		if (delay > System.currentTimeMillis()) {
			return false;
		}

		var now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

		for (var time : startTime) {
			var hourMinute = time.split(":");

			if (Integer.parseInt(hourMinute[0].trim()) == now.getHour() && Integer.parseInt(hourMinute[1].trim()) == now.getMinute()) {
				delay = System.currentTimeMillis() + DateUtils.MILLIS_PER_MINUTE;
				RedisHelper.putString(getTaskName(), Long.toString(delay));

				return true;
			}
		}

		return false;
	}

	protected String getTaskName() {
		return getClass().getName();
	}
}
