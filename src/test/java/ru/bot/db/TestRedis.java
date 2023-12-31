package ru.bot.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestRedis {

	@AfterEach
	public void deleteTestData() {
		RedisHelper.deleteValue("test");
	}

	@Test
	public void testInsert() {
		RedisHelper.putString("test", "test");

		var value = RedisHelper.getString("test");
		Assertions.assertEquals(value, "test");
	}

	@Test
	public void testTmp() {
		try {
			var value = RedisHelper.getCollection("allowableChatIds");

			System.err.println(value);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
