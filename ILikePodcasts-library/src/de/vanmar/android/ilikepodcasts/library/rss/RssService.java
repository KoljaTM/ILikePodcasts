package de.vanmar.android.ilikepodcasts.library.rss;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

import de.vanmar.android.ilikepodcasts.library.IRssService;
import de.vanmar.android.ilikepodcasts.library.IRssService.Callback;
import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EService
public class RssService extends Service {

	@Bean
	UiHelper uiHelper;

	@Bean
	RssLoader rssLoader;

	@Bean
	DatabaseManager dbManager;

	private final RssServiceBinder myServiceBinder = new RssServiceBinder();

	@Override
	public IBinder onBind(final Intent intent) {
		return myServiceBinder;
	}

	public class RssServiceBinder extends Binder implements IRssService {

		private final Set<Callback> callbacks = new HashSet<IRssService.Callback>();

		@Override
		public void registerCallback(final Callback callback) {
			callbacks.add(callback);
		}

		@Override
		public void unRegisterCallback(final Callback callback) {
			callbacks.remove(callback);
		}

		@Override
		public void updateFeeds() throws Exception {
			refreshFeeds();
		}
	}

	private void publishFeedUpdateStarted(final Feed feed) {
		for (final Callback callback : myServiceBinder.callbacks) {
			callback.onFeedUpdateStarted(feed);
		}
	}

	private void publishFeedUpdateComplete(final Feed feed) {
		for (final Callback callback : myServiceBinder.callbacks) {
			callback.onFeedUpdateCompleted(feed);
		}
	}

	public void refreshFeeds() throws Exception {
		final List<Feed> feeds = dbManager.getAllFeeds();
		for (final Feed feed : feeds) {
			publishFeedUpdateStarted(feed);
			rssLoader.updateFeed(feed);
			publishFeedUpdateComplete(feed);
		}
		refreshContentProvider();
	}

	private void refreshContentProvider() {
		getApplicationContext().getContentResolver().notifyChange(
				Uri.parse(getApplicationContext().getString(
						R.string.episodeContentProviderUri)), null);
	}
}
