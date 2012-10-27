package de.vanmar.android.ilikepodcasts.library;

import de.vanmar.android.ilikepodcasts.library.bo.Item;

public interface IMediaPlayerService {

	public interface Callback {
		void playStarted(Item item, int totalDuration);

		void playPaused();
	}

	void registerCallback(Callback callback);

	void unRegisterCallback(Callback callback);

	void play();

	void play(Item item);

	void pause();

	void skipForward();

	void skipBack();

	int getPlaybackPosition();

	void seekToPosition(int position);

}
