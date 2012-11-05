package de.vanmar.android.ilikepodcasts.library;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

import de.vanmar.android.ilikepodcasts.library.IDownloadService.Callback;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EService
public class DownloadService extends Service {

	public static final int UPDATE_PROGRESS = 8344;

	private static final String FILE_PREFIX = "Podcast_";

	public static final String EXTRA_ITEM = "de.vanmar.android.ilikepodcasts.downloadservice.item";

	@Bean
	UiHelper uiHelper;

	private static final int DOWNLOAD_NOTIFICATION = 34;
	private DownloadServiceBinder myServiceBinder = new DownloadServiceBinder();

	private final Queue<Item> downloadQueue = new LinkedList<Item>();

	private boolean downloading = false;

	@Background
	void startDownload(final Item itemToDownload) {
		OutputStream output = null;
		InputStream input = null;
		try {
			final URL url = new URL(itemToDownload.getMediaUrl());
			final URLConnection connection = url.openConnection();
			connection.connect();
			// this will be useful so that you can show a typical 0-100%
			// progress bar
			final int fileLength = connection.getContentLength();

			// create output location
			final File SDCardRoot = Environment.getExternalStorageDirectory();
			// create a new file, specifying the path, and the filename
			// which we want to save the file as.
			new File(SDCardRoot, ILikePodcastsApplication.FILE_DIR).mkdir();

			// download the file
			input = new BufferedInputStream(url.openStream());
			final String filename = ILikePodcastsApplication.FILE_DIR
					+ File.separator + FILE_PREFIX
					+ itemToDownload.getMediaUrl().hashCode() + ".mp3";
			output = new FileOutputStream(new File(SDCardRoot, filename));

			final byte data[] = new byte[1024];
			int total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				output.write(data, 0, count);
				// publishing the progress....
				publishProgress(itemToDownload, total, fileLength);
			}
			publishDownloadComplete(itemToDownload);
			itemToDownload.setMediaPath(filename);
			DatabaseManager.getInstance().saveItem(itemToDownload);
			getContentResolver().notifyChange(
					Uri.parse(getString(R.string.episodeContentProviderUri)),
					null);
		} catch (final Exception e) {
			uiHelper.displayError(e);
		} finally {
			if (output != null) {
				try {
					output.flush();
					output.close();
				} catch (final Exception e) {
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (final Exception e) {
				}
			}
		}
		downloading = false;
		checkQueue();
	}

	private void publishProgress(final Item itemToDownload, final int progress,
			final int total) {
		for (final Callback callback : myServiceBinder.callbacks) {
			callback.onDownloadProgress(itemToDownload, progress, total);
		}
	}

	private void publishDownloadComplete(final Item itemToDownload) {
		for (final Callback callback : myServiceBinder.callbacks) {
			callback.onDownloadCompleted(itemToDownload);
		}
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
