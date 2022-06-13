package ru.bot.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import ru.bot.db.DBHelper;
import ru.bot.errors.BotErrorException;

public class SendGoodMorningMessageTask extends AbstractSendTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendGoodMorningMessageTask.class);

	private final TelegramLongPollingBot bot;

	public SendGoodMorningMessageTask(TelegramLongPollingBot bot) {
		super("goodMorningTimes");
		this.bot = bot;
	}

	@Override
	public void run() {
		try {
			if (!canStartNow()) {
				return;
			}

			for (var chatId : DBHelper.INSTANCE.getCollection("allowableChatIds")) {
				bot.execute(new SendMessage(chatId, DBHelper.INSTANCE.getString("goodMorningMessage")));
			}
		} catch (Exception e) {
			LOGGER.error("failed to process task: SendGoodMorningMessageTask", e);
			throw BotErrorException.valueOf(e);
		}
	}
}
