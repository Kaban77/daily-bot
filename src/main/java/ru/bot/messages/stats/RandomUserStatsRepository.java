package ru.bot.messages.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bot.db.RedisHelper;

public class RandomUserStatsRepository {
	private static final String PROPERY_NAME = "randomUserStats";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static RandomUserStats getUserStats(ChatMember user, String chatId) throws JsonProcessingException {
		var map = RedisHelper.getMap(PROPERY_NAME);
		if (map == null) {
			return fillNewRandomUserStats(user);
		}

		var chatStats = map.get(chatId);
		if (StringUtils.isBlank(chatStats)) {
			return fillNewRandomUserStats(user);
		}

		var json = new JSONObject(chatStats);
		if (!json.has(user.getUser().getId().toString())) {
			return fillNewRandomUserStats(user);
		}

		var node = json.getJSONObject(user.getUser().getId().toString());

		return OBJECT_MAPPER.readValue(node.toString(), RandomUserStats.class);
	}

	public static void saveUserStats(RandomUserStats randomUserStats, String chatId) throws JsonProcessingException, JSONException {
		var map = RedisHelper.getMap(PROPERY_NAME);
		if (map == null) {
			map = new HashMap<>();
		}

		var chatStats = map.get(chatId);
		var json = chatStats != null ? new JSONObject(chatStats) : new JSONObject();
		json.put(randomUserStats.getUserId().toString(), new JSONObject(OBJECT_MAPPER.writeValueAsString(randomUserStats)));

		var hashMap = new HashMap<String, String>(map);
		hashMap.put(chatId, json.toString());
		RedisHelper.setMap(PROPERY_NAME, hashMap);
	}

	public static List<RandomUserStats> getChatStats(String chatId) throws JsonMappingException, JsonProcessingException {
		var map = RedisHelper.getMap(PROPERY_NAME);
		if (map == null) {
			return List.of();
		}

		var chatStats = map.get(chatId);
		if (chatStats == null) {
			return List.of();
		}

		var jsonTree = OBJECT_MAPPER.readTree(chatStats);
		if (jsonTree.isEmpty()) {
			return List.of();
		}

		var result = new ArrayList<RandomUserStats>();
		var fields = jsonTree.fields();
		while (fields.hasNext()) {
			result.add(OBJECT_MAPPER.treeToValue(fields.next().getValue(), RandomUserStats.class));
		}

		result.sort((u1, u2) -> {
			if (u1.getCount() > u2.getCount()) {
				return -1;
			}

			if (u1.getCount() < u2.getCount()) {
				return 1;
			}

			return Long.compare(u1.getUserId(), u2.getUserId());
		});

		return result;
	}

	public static void clearMap() {
		RedisHelper.deleteValue(PROPERY_NAME);
	}

	private static RandomUserStats fillNewRandomUserStats(ChatMember user) {
		var randomUserStats = new RandomUserStats();

		randomUserStats.setUserId(user.getUser().getId());
		randomUserStats.setName(user.getUser().getFirstName());

		return randomUserStats;
	}
}
