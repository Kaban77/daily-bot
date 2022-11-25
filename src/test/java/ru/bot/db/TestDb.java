package ru.bot.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestDb {

	@AfterEach
	public void deleteTestData() {
		DBHelper.INSTANCE.deleteValue("test");
	}

	@Test
	public void testInsert() {
		DBHelper.INSTANCE.putString("test", "test");

		var value = DBHelper.INSTANCE.getString("test");
		Assertions.assertEquals(value, "test");
	}

	@Test
	public void testTmp() {
		try {
			var value = DBHelper.INSTANCE.getCollection("allowableChatIds");

			System.err.println(value);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
