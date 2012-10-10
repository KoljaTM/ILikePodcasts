package de.vanmar.android.ilikepodcasts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

import de.vanmar.android.ilikepodcasts.util.UiHelper;

@EService
public class DownloadService extends IntentService {

	public static final int UPDATE_PROGRESS = 8344;

	@Bean
	UiHelper uiHelper;

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		final String urlToDownload = intent.getStringExtra("url");
		final ResultReceiver receiver = (ResultReceiver) intent
				.getParcelableExtra("receiver");

		OutputStream output = null;
		InputStream input = null;
		try {
			final URL url = new URL(urlToDownload);
			final URLConnection connection = url.openConnection();
			connection.connect();
			// this will be useful so that you can show a typical 0-100%
			// progress bar
			final int fileLength = connection.getContentLength();

			// create output location
			final File SDCardRoot = Environment.getExternalStorageDirectory();
			// create a new file, specifying the path, and the filename
			// which we want to save the file as.
			new File(SDCardRoot, "ILikePodcasts").mkdir();

			// download the file
			input = new BufferedInputStream(url.openStream());
			output = new FileOutputStream(new File(SDCardRoot,
					"ILikePodcasts/test2.mp3"));

			final byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				final Bundle resultData = new Bundle();
				resultData.putInt("progress", (int) (total * 100 / fileLength));
				receiver.send(UPDATE_PROGRESS, resultData);
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		} catch (final IOException e) {
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

		final Bundle resultData = new Bundle();
		resultData.putInt("progress", 100);
		receiver.send(UPDATE_PROGRESS, resultData);
	}

}
