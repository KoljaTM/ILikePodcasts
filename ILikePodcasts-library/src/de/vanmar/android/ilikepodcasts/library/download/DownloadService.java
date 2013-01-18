package de.vanmar.android.ilikepodcasts.library.download;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

import de.vanmar.android.ilikepodcasts.library.IDownloadService;
import de.vanmar.android.ilikepodcasts.library.MainActivity_;
import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EService
public class DownloadService extends Service {

	public static final int UPDATE_PROGRESS = 8344;

	public static final String EXTRA_ITEM = "de.vanmar.android.ilikepodcasts.downloadservice.item";

	@Bean
	UiHelper uiHelper;

	@Bean
	Downloader downloader;

	private static final int DOWNLOAD_NOTIFICATION = 34;
	private final DownloadServiceBinder myServiceBinder = new DownloadServiceBinder();

	private final Queue<Item> downloadQueue = new LinkedList<Item>();

	private boolean downloading = false;

	@Background
	void startDownload(final Item itemToDownload) {
		try {
			downloader.download(itemToDownload, myServiceBinder.callbacks);
			getContentResolver().notifyChange(
					Uri.parse(getString(R.string.episodeContentProviderUri)),
					null);
		} catch (final Exception e) {
			uiHelper.displayError(e);
		}
		downloading = false;
		checkQueue();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return myServiceBinder;
	}

	public void checkQueue() {
		if (downloading) {
			return;
		}
		final Item itemToDownload = downloadQueue.poll();
		if (itemToDownload == null) {
			stopForeground(true);
			stopSelf();
			return;
		}
		downloading = true;
		inForeground(itemToDownload);
		startDownload(itemToDownload);
	}

	private void inForeground(final Item item) {
		final Intent notificationIntent = new Intent(this, MainActivity_.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		final Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.logo)
				.setTicker(getText(R.string.downloadingTitle))
				.setContentTitle(getText(R.string.downloadingNotificationTitle))
				.setContentText(
						getString(R.string.downloadingNotificationText,
								item.getTitle()))
				.setContentIntent(pendingIntent).getNotification();
		startForeground(DOWNLOAD_NOTIFICATION, notification);
	}

	public class DownloadServiceBinder extends Binder implements
			IDownloadService {

		private final Set<Callback> callbacks = new HashSet<IDownloadService.Callback>();

		@Override
		public void registerCallback(final Callback callback) {
			callbacks.add(callback);
		}

		@Override
		public void unRegisterCallback(final Callback callback) {
			callbacks.remove(callback);
		}

		@Override
		public void startDownload(final Item item) {
			downloadQueue.add(item);
			DownloadService.this.checkQueue();
		}
	}
}
