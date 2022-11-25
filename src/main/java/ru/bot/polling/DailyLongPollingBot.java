package ru.bot.polling;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.bot.db.DBHelper;
import ru.bot.errors.BotErrorException;
import ru.bot.errors.BotErrors;

public class DailyLongPollingBot extends TelegramLongPollingBot {
	
	private static final String BOT_USERNAME = DBHelper.INSTANCE.getString("fastMelodicBotName");
	private static final String BOT_TOKEN = DBHelper.INSTANCE.getString("fastMelodicBotToken");
	private static final Collection<String> ALLOWABLE_CHAT_IDS = DBHelper.INSTANCE.getCollection("allowableChatIds");
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DailyLongPollingBot.class);

	@Override
	public void onUpdateReceived(Update update) {
		if (!update.hasMessage()) {
			return;
		}

		if (!ALLOWABLE_CHAT_IDS.contains(update.getMessage().getChatId().toString())) {
			return;
		}

		LOGGER.debug("Received message: " + update);

		try {
			var message = StringUtils.replace(update.getMessage().getText(), "@" + getBotUsername(), StringUtils.EMPTY);

			if (StringUtils.equalsIgnoreCase(message, "нет")) {
				execute(SendMessage
						.builder()
						.chatId(update.getMessage().getChatId().toString())
						.replyToMessageId(update.getMessage().getMessageId())
						.text(DBHelper.INSTANCE.getString("noAnswer"))
						.build());
			}
		} catch (Exception e) {
			throw new BotErrorException("Failed to process message: " + update, BotErrors.DEFAULT, e);
		}
	}

	@Override
	public String getBotUsername() {
		return BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}

}
