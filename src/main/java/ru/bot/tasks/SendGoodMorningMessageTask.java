package ru.bot.tasks;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import ru.bot.db.DBHelper;
import ru.bot.errors.BotErrorException;

public class SendGoodMorningMessageTask extends AbstractSendTask {

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
			BotErrorException.valueOf(e);
		}
	}
}
