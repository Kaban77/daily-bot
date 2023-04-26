package ru.bot.locks;

import org.apache.commons.lang3.StringUtils;

import ru.bot.db.RedisHelper;

public class BotLock {

	private static final String LOCK = "lock";

	public static boolean isLocked(String processId) {
		if (!RedisHelper.INSTANCE.exists(LOCK)) {
			return false;
		}

		var currentProcessId = RedisHelper.INSTANCE.getString(LOCK);
		return StringUtils.equals(processId, currentProcessId);
	}

	public static boolean tryLock(String processId) {
		if (RedisHelper.INSTANCE.exists(LOCK)) {
			return false;
		}

		RedisHelper.INSTANCE.putString(LOCK, processId, 300 * 1000L);
		return true;
	}

	public static void unlock(String processId) {
		if (!RedisHelper.INSTANCE.exists(LOCK)) {
			return;
		}

		var currentProcessId = RedisHelper.INSTANCE.getString(LOCK);
		if (StringUtils.equals(processId, currentProcessId)) {
			RedisHelper.INSTANCE.deleteValue(LOCK);
		}
	}
}
