package ru.bot.messages.tasks;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.fasterxml.jackson.core.JsonProcessingException;

import ru.bot.chats.AllowableChatsHelper;
import ru.bot.db.RedisHelper;
import ru.bot.errors.BotErrorException;
import ru.bot.messages.stats.RandomUserStats;
import ru.bot.messages.stats.RandomUserStatsRepository;
import ru.bot.random.RandomHelper;

public class SendRandomUserMessage extends AbstractSendTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendRandomUserMessage.class);
	private static final String MESSAGE = RedisHelper.getString("randomUserMessage");

	private final TelegramLongPollingBot bot;
	private final CallableWithRetry callableWithRetry;

	public SendRandomUserMessage(TelegramLongPollingBot bot, CallableWithRetry callableWithRetry) {
		super("randomUserTimes");

		this.bot = bot;
		this.callableWithRetry = callableWithRetry;
	}

	@Override
	public void run() {
		try {
			if (!canStartNow()) {
				return;
			}

			var random = RandomHelper.getRandom();

			for (var chatId : AllowableChatsHelper.getAllowableChats()) {
				var users = AllowableChatsHelper.getChatUsers(chatId);
				if (users.size() <= 2) {
					continue;
				}

				var userId = users.get(random.nextInt(0, users.size()));
				var user = sendUserMessage(userId, chatId);
				
				var statsMessage = buildStatsMessage(user, chatId);
				sendStatsMessage(statsMessage, chatId);
			}
		} catch (Exception e) {
			LOGGER.error("failed to process task: SendRandomUserMessage", e);
		}
	}

	private String buildLink(ChatMember user) {
		var link = new StringBuilder();
		link.append("<a href=\"tg://user?id=")
			.append(user.getUser().getId())
			.append("\">")
			.append(user.getUser().getFirstName())
			.append("</a>");


		return link.toString();
	}

	private String buildStatsMessage(ChatMember chatMember, String chatId) throws JsonProcessingException {
		var userStats = incrementAndGetStats(chatMember, chatId);
		if (userStats == null || userStats.isEmpty()) {
			return null;
		}

		var message = new StringBuilder("Статистика:\n");

		for (var user : userStats) {
			message.append("<strong>")
				.append(user.getName())
				.append("</strong>")
				.append(" - ")
				.append(user.getCount())
				.append("\n");

		}

		return message.toString();
	}

	private List<RandomUserStats> incrementAndGetStats(ChatMember chatMember, String chatId) throws JsonProcessingException {
		var randomUserStats = RandomUserStatsRepository.getUserStats(chatMember, chatId);
		randomUserStats.setCount(randomUserStats.getCount() + 1);
		RandomUserStatsRepository.saveUserStats(randomUserStats, chatId);

		return RandomUserStatsRepository.getChatStats(chatId);
	}

	private ChatMember sendUserMessage(Long userId, String chatId) {
		return callableWithRetry.callWithRetry(() -> {
			try {
				var user = bot.execute(new GetChatMember(chatId, userId));
	
				bot.execute(SendMessage
						.builder()
						.chatId(chatId)
						.text(buildLink(user) + StringUtils.SPACE + MESSAGE)
						.parseMode("HTML")
						.build());

				return user;
			} catch (TelegramApiException e) {
				throw BotErrorException.valueOf(e);
			}
		});
	}

	private void sendStatsMessage(String statsMessage, String chatId) {
		if (statsMessage != null) {
			callableWithRetry.callWithRetry(() -> {
				bot.execute(SendMessage
					.builder()
					.chatId(chatId)
					.text(statsMessage)
					.parseMode("HTML")
					.build());

				return null;
			});

		}
	}

}
