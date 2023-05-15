package ru.bot.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import ru.bot.errors.BotErrorException;
import ru.bot.errors.BotErrors;

public class RedisHelper {

	public static final RedisHelper INSTANCE = new RedisHelper();
	
	private final String dbUrl;
	private final String dbUsername;
	private final String dbPassword;
	private final int dbPort;

	private RedisHelper() {
		ResourceBundle resource;
		try {
			resource = ResourceBundle.getBundle("db");
		} catch (MissingResourceException e) {
			resource = null;
		}

		if (resource != null) {
			dbUrl = resource.getString("db.url");
			dbUsername = resource.getString("db.user");
			dbPassword = resource.getString("db.password");
			dbPort = Integer.parseInt(resource.getString("db.port"));
		} else {
			dbUrl = null;
			dbUsername = null;
			dbPassword = null;
			dbPort = -1;
		}
	}

	public void deleteValue(String key) {
		try (var jedis = getJedis()) {
			jedis.del(key);
		}
	}

	public boolean exists(String key) {
		try (var jedis = getJedis()) {
			return jedis.exists(key);
		}
	}

	public String getString(String key) {
		try (var jedis = getJedis()) {
			return jedis.get(key);
		}
	}
	
	public void putString(String key, String value) {
		try (var jedis = getJedis()) {
			jedis.set(key, value);
		}
	}
	
	public void putString(String key, String value, long millisecondsToExpire) {
		try (var jedis = getJedis()) {
			jedis.set(key, value, SetParams.setParams().px(millisecondsToExpire));
		}
	}

	public Collection<String> getCollection(String key) {
		try (var jedis = getJedis()) {
			var tmp = jedis.hgetAll(key);
			return tmp.values();
		}
	}

	public void setCollection(String key, Collection<String> values) {
		if (values == null || values.isEmpty()) {
			return;
		}
		try (var jedis = getJedis()) {
			var map = new HashMap<String, String>();
			
			int i = 0;
			for (var value : values) {
				map.put(Integer.toString(i + 1), value);
				i++;
			}
			jedis.hset(key, map);
		}
	}

	public Map<String, String> getMap(String key) {
		try (var jedis = getJedis()) {
			return jedis.hgetAll(key);
		}
	}

	public void setMap(String key, Map<String, String> map) {
		try (var jedis = getJedis()) {
			jedis.hset(key, map);
		}
	}

	private Jedis getJedis() {
		if (dbUrl == null || dbUsername == null || dbPassword == null) {
			throw new BotErrorException("Config file wasn't found", BotErrors.CONFIG_FILE_NOT_FOUND);
		}

		return new Jedis(dbUrl, dbPort,
				DefaultJedisClientConfig
				.builder()
				.user(dbUsername)
				.password(dbPassword)
				.build());
	}

}
