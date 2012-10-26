package de.vanmar.android.ilikepodcasts.library;

import de.vanmar.android.ilikepodcasts.library.bo.Item;

public interface IMediaPlayerService {

	public interface Callback {
		void playStarted();
	}

	void play();

	void play(Item item);

	void registerCallback(Callback callback);

	void unRegisterCallback(Callback callback);

}
