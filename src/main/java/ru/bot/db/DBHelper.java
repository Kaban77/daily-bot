package ru.bot.db;

import java.util.Collection;
import java.util.ResourceBundle;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

public class DBHelper {

	public static final DBHelper INSTANCE = new DBHelper();
	
	private final String dbUrl;
	private final String dbUsername;
	private final String dbPassword;
	private final int dbPort;

	private DBHelper() {
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
