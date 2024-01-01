package ru.bot.messages.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import ru.bot.chats.AllowableChatsHelper;
import ru.bot.db.RedisHelper;

public class Send4Task extends AbstractSendTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(Send4Task.class);
	private static final String STICKER_ID = RedisHelper.getString("fourStickerFileId");

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

			for (var chatId : AllowableChatsHelper.getAllowableChats()) {
				bot.execute(new SendSticker(chatId, new InputFile(STICKER_ID)));
			}

		} catch (Exception e) {
			LOGGER.error("failed to process task: Send4Task", e);
		}
	}

}
