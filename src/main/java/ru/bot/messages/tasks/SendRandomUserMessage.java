package ru.bot.messages.tasks;

import java.util.random.RandomGeneratorFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import ru.bot.chats.AllowableChatsHelper;
import ru.bot.db.RedisHelper;

public class SendRandomUserMessage extends AbstractSendTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendRandomUserMessage.class);

	private final TelegramLongPollingBot bot;

	public SendRandomUserMessage(TelegramLongPollingBot bot) {
		super("randomUserTimes");
		this.bot = bot;
	}

	@Override
	public void run() {
		try {
			if (!canStartNow()) {
				return;
			}

			var random = RandomGeneratorFactory.getDefault().create();

			for (var chatId : AllowableChatsHelper.getAllowableChats()) {
				var users = AllowableChatsHelper.getChatUsers(chatId);
				if (users.size() <= 2) {
					continue;
				}

				var userId = users.get(random.nextInt(0, users.size()));
				var user = bot.execute(new GetChatMember(chatId, userId));

				bot.execute(SendMessage
						.builder()
						.chatId(chatId)
						.text(buildLink(user) + StringUtils.SPACE + RedisHelper.INSTANCE.getString("randomUserMessage"))
						.parseMode("HTML")
						.build());
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
}
