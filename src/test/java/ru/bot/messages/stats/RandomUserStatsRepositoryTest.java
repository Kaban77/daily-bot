package ru.bot.messages.stats;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberMember;

import ru.bot.db.RedisHelper;

public class RandomUserStatsRepositoryTest {
	private static final String PROPERY_NAME = "randomUserStats";

	@Test
	void mapWasntFilledTest() throws Exception {
		try (var mockRedisHelper = mockStatic(RedisHelper.class)) {
			var user = new User();
			user.setId(1L);
			user.setFirstName("name");

			var chatMember = ChatMemberMember.builder()
					.user(user)
					.build();

			mockRedisHelper.when(() -> RedisHelper.getMap(PROPERY_NAME)).thenReturn(null);
			var result = RandomUserStatsRepository.getUserStats(chatMember, "test");

			assertNotNull(result);
			assertEquals(1L, result.getUserId());
			assertEquals(0, result.getCount());
			assertEquals("name", result.getName());
		}
	}

	@Test
	void mapIsEmptyTest() throws Exception {
		try (var mockRedisHelper = mockStatic(RedisHelper.class)) {
			var user = new User();
			user.setId(1L);
			user.setFirstName("name");

			var chatMember = ChatMemberMember
					.builder()
					.user(user)
					.build();

			mockRedisHelper.when(() -> RedisHelper.getMap(PROPERY_NAME)).thenReturn(Map.of());
			var result = RandomUserStatsRepository.getUserStats(chatMember, "test");

			assertNotNull(result);
			assertEquals(1L, result.getUserId());
			assertEquals(0, result.getCount());
			assertEquals("name", result.getName());
		}
	}

	@Test
	void emptyJsonTest() throws Exception {
		try (var mockRedisHelper = mockStatic(RedisHelper.class)) {
			var user = new User();
			user.setId(1L);
			user.setFirstName("name");

			var chatMember = ChatMemberMember
					.builder()
					.user(user)
					.build();

			var json = "{}";

			mockRedisHelper.when(() -> RedisHelper.getMap(PROPERY_NAME)).thenReturn(Map.of("test", json));
			var result = RandomUserStatsRepository.getUserStats(chatMember, "test");

			assertNotNull(result);
			assertEquals(1L, result.getUserId());
			assertEquals(0, result.getCount());
			assertEquals("name", result.getName());
		}
	}
	
	@Test
	void userDoesntExist() throws Exception {
		try (var mockRedisHelper = mockStatic(RedisHelper.class)) {
			var user = new User();
			user.setId(1L);
			user.setFirstName("name");

			var chatMember = ChatMemberMember
					.builder()
					.user(user)
					.build();

			var json = "{\"anotherId\": {\"userId\": 100500,\"name\":\"anotherName\", \"count\": 10}}";

			mockRedisHelper.when(() -> RedisHelper.getMap(PROPERY_NAME)).thenReturn(Map.of("test", json));
			var result = RandomUserStatsRepository.getUserStats(chatMember, "test");

			assertNotNull(result);
			assertEquals(1L, result.getUserId());
			assertEquals(0, result.getCount());
			assertEquals("name", result.getName());
		}
	}
	
	@Test
	void userExistsTest() throws Exception {
		try (var mockRedisHelper = mockStatic(RedisHelper.class)) {
			var user = new User();
			user.setId(1L);
			user.setFirstName("name");

			var chatMember = ChatMemberMember
					.builder()
					.user(user)
					.build();

			var json = "{\"1\": {\"userId\": 1,\"name\":\"name\", \"count\": 10}}";

			mockRedisHelper.when(() -> RedisHelper.getMap(PROPERY_NAME)).thenReturn(Map.of("test", json));
			var result = RandomUserStatsRepository.getUserStats(chatMember, "test");

			assertNotNull(result);
			assertEquals(1L, result.getUserId());
			assertEquals(10, result.getCount());
			assertEquals("name", result.getName());
		}
	}

	@Test
	void saveNewStatsTest() throws Exception {
		try (var mockRedisHelper = mockStatic(RedisHelper.class)) {
			var randomUserStats = new RandomUserStats();
			randomUserStats.setUserId(1L);
			randomUserStats.setCount(10);
			randomUserStats.setName("name");

			mockRedisHelper.when(() -> RedisHelper.getMap(PROPERY_NAME)).thenReturn(null);

			assertDoesNotThrow(() -> RandomUserStatsRepository.saveUserStats(randomUserStats, "test"));
		}
	}

	@Test
	void getChatStatsTest() throws Exception {
		try (var mockRedisHelper = mockStatic(RedisHelper.class)) {
			var randomUserStats = new RandomUserStats();
			randomUserStats.setUserId(1L);
			randomUserStats.setCount(10);
			randomUserStats.setName("name");

			var json = "{\"1\": {\"userId\": 1,\"name\":\"name\", \"count\": 10}, \"2\": {\"userId\": 2,\"name\":\"name2\", \"count\": 8}}";

			mockRedisHelper.when(() -> RedisHelper.getMap(PROPERY_NAME)).thenReturn(Map.of("test", json));

			var result = RandomUserStatsRepository.getChatStats("test");

			assertNotNull(result);
			assertEquals(2, result.size());

			var value = result.get(0);

			assertEquals(randomUserStats, value);
		}
	}
}
