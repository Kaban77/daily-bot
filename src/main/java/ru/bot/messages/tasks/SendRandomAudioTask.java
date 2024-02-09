package ru.bot.messages.tasks;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import ru.bot.chats.AllowableChatsHelper;
import ru.bot.db.RedisHelper;
import ru.bot.random.RandomHelper;

public class SendRandomAudioTask extends AbstractSendTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendRandomAudioTask.class);

	private final TelegramLongPollingBot bot;

	public SendRandomAudioTask(TelegramLongPollingBot bot) {
		super("randomAudioTimes");
		this.bot = bot;
	}

	@Override
	public void run() {
		try {
			if (!canStartNow()) {
				return;
			}

			var audios = RedisHelper.getCollection("audios");
			if (audios == null || audios.isEmpty()) {
				return;
			}

			var audiosList = new ArrayList<>(audios);
			var random = RandomHelper.getRandom();
			var text = RedisHelper.getString("randomAudioMessage");

			for (var chatId : AllowableChatsHelper.getAllowableChats()) {
				var randomId = audiosList.get(random.nextInt(0, audiosList.size()));
				bot.execute(new SendMessage(chatId, text));
				bot.execute(new SendAudio(chatId, new InputFile(randomId)));
			}

		} catch (Exception e) {
			LOGGER.error("failed to process task: " + getTaskName(), e);
		}

	}

}
