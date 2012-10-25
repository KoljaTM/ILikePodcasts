package de.vanmar.android.ilikepodcasts.library;

public interface IMediaPlayerService {

	public interface Callback {
		void playStarted();
	}

	void play();

	void registerCallback(Callback callback);

	void unRegisterCallback(Callback callback);
}
