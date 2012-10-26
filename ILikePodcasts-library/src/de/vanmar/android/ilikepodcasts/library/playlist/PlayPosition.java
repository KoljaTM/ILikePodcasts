package de.vanmar.android.ilikepodcasts.library.playlist;

import de.vanmar.android.ilikepodcasts.library.bo.Item;

public class PlayPosition {

	public PlayPosition(final Item item, final int position) {
		super();
		this.item = item;
		this.position = position;
	}

	private Item item;

	private int position;

	public Item getItem() {
		return item;
	}

	public void setItem(final Item item) {
		this.item = item;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(final int position) {
		this.position = position;
	}

}
