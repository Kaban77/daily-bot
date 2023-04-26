package ru.bot.db;

import java.util.Collection;
import java.util.ResourceBundle;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class RedisHelper {

	public static final RedisHelper INSTANCE = new RedisHelper();
	
	private final String dbUrl;
	private final String dbUsername;
	private final String dbPassword;
	private final int dbPort;

	private RedisHelper() {
		var resource = ResourceBundle.getBundle("db");

		dbUrl = resource.getString("db.url");
		dbUsername = resource.getString("db.user");
		dbPassword = resource.getString("db.password");
		dbPort = Integer.parseInt(resource.getString("db.port"));
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

	private Jedis getJedis() {
		return new Jedis(dbUrl, dbPort,
				DefaultJedisClientConfig
				.builder()
				.user(dbUsername)
				.password(dbPassword)
				.build());
	}

}
