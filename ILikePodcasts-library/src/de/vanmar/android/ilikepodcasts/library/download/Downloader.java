package de.vanmar.android.ilikepodcasts.library.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Set;

import android.os.Environment;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

import de.vanmar.android.ilikepodcasts.library.IDownloadService.Callback;
import de.vanmar.android.ilikepodcasts.library.ILikePodcastsApplication;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.playlist.PlaylistManager;

@EBean
public class Downloader {

	private static final String FILE_PREFIX = "Podcast_";

	@Bean
	DatabaseManager dbManager;

	@Bean
	PlaylistManager playlistManager;

	public void download(final Item itemToDownload,
			final Set<Callback> callbacks) throws IOException, SQLException {
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
			output = new BufferedOutputStream(new FileOutputStream(new File(
					SDCardRoot, filename)), 8192);

			final byte[] data = new byte[1024];
			int total = 0;
			int count = input.read(data);
			while (count != -1) {
				total += count;
				output.write(data, 0, count);
				// publishing the progress....
				publishProgress(itemToDownload, total, fileLength, callbacks);
				count = input.read(data);
			}
			publishDownloadComplete(itemToDownload, callbacks);
			itemToDownload.setMediaPath(filename);
			dbManager.saveItem(itemToDownload);
			playlistManager.enqueueItem(itemToDownload);
		} finally {
			if (output != null) {
				try {
					output.flush();
					output.close();
				} catch (final Exception e) {
					Log.e("DownloadService",
							"Exception while closing OutputStream", e);
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (final Exception e) {
					Log.e("DownloadService",
							"Exception while closing InputStream", e);
				}
			}
		}
	}

	private void publishProgress(final Item itemToDownload, final int progress,
			final int total, final Set<Callback> callbacks) {
		for (final Callback callback : callbacks) {
			callback.onDownloadProgress(itemToDownload, progress, total);
		}
	}

	private void publishDownloadComplete(final Item itemToDownload,
			final Set<Callback> callbacks) {
		for (final Callback callback : callbacks) {
			callback.onDownloadCompleted(itemToDownload);
		}
	}

}
