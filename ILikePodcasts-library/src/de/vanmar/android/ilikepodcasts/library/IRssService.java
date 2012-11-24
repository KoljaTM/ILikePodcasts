package de.vanmar.android.ilikepodcasts.library;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;

public interface IRssService {

	public interface Callback {
		void onFeedUpdateStarted(Feed feed);

		void onFeedUpdateCompleted(Feed feed);
	}

	void registerCallback(Callback callback);

	void unRegisterCallback(Callback callback);

	void updateFeeds() throws Exception;

}
