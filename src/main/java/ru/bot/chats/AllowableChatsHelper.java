package ru.bot.chats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ru.bot.db.RedisHelper;

public class AllowableChatsHelper {
	private static final Set<String> ALLOWABLE_CHAT_IDS = new HashSet<>(RedisHelper.getCollection("allowableChatIds"));
	private static final ConcurrentMap<String, List<Long>> ACTIVE_CHAT_USERS = new ConcurrentHashMap<>();

	public static Set<String> getAllowableChats() {
		return ALLOWABLE_CHAT_IDS;
	}

	public static List<Long> getChatUsers(String chatId) {
		if (!ACTIVE_CHAT_USERS.containsKey(chatId)) {
			ACTIVE_CHAT_USERS.put(chatId, new ArrayList<Long>());
		}

		return ACTIVE_CHAT_USERS.get(chatId);
	}

	public static void setChatUser(String chatId, Long userId) {
		var users = ACTIVE_CHAT_USERS.get(chatId);
		if (users == null) {
			users = new ArrayList<Long>();
			users.add(userId);

			ACTIVE_CHAT_USERS.put(chatId, users);
		} else {
			var tmpSet = new HashSet<Long>(users);
			if (!tmpSet.contains(userId)) {
				users.add(userId);
			}
		}
	}
}
