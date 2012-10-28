package de.vanmar.android.ilikepodcasts.library;

import de.vanmar.android.ilikepodcasts.library.bo.Item;

public class PlayerStatus {

	public enum PlayerState {
		STOPPED, STARTED
	}

	private Item item;
	private int totalDuration = 0;
	private int progress = 0;
	private PlayerState state = PlayerState.STOPPED;

	public Item getItem() {
		return item;
	}

	public void setItem(final Item item) {
		this.item = item;
	}

	public int getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(final int totalDuration) {
		this.totalDuration = totalDuration;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(final int progress) {
		this.progress = progress;
	}

	public PlayerState getState() {
		return state;
	}

	public void setState(final PlayerState state) {
		this.state = state;
	}
}
