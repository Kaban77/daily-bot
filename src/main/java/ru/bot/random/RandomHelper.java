package ru.bot.random;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomHelper {

	public static RandomGenerator getRandom() {
		return RandomGeneratorFactory.all()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Random wasn't found"))
				.create();
	}
}
