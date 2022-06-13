package ru.bot.tasks;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import ru.bot.db.DBHelper;
import ru.bot.errors.BotErrorException;

public class Send4Task extends AbstractSendTask {

	private final TelegramLongPollingBot bot;

	public Send4Task(TelegramLongPollingBot bot) {
		super("fourTimes");
		this.bot = bot;
	}

	@Override
	public void run() {
		try {
			if (!canStartNow()) {
				return;
			}

			for (var chatId : DBHelper.INSTANCE.getCollection("allowableChatIds")) {
				bot.execute(new SendSticker(chatId, new InputFile(DBHelper.INSTANCE.getString("fourStickerFileId"))));
			}

		} catch (Exception e) {
			throw BotErrorException.valueOf(e);
		}
	}

}
