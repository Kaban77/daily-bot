package ru.bot.messages.stats;

import java.util.Objects;

public class RandomUserStats {
	private Long userId;
	private String name;
	private int count;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, name, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RandomUserStats other = (RandomUserStats) obj;
		return count == other.count && Objects.equals(name, other.name) && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		return "RandomUserStats [userId=" + userId + ", name=" + name + ", count=" + count + "]";
	}

}
