package de.vanmar.android.ilikepodcasts.library;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@SuppressLint("Registered")
@EService
public class MediaPlayerService extends Service {

	@Bean
	UiHelper uiHelper;

	public static final String EXTRA_ITEM = "de.vanmar.android.ilikepodcasts.mediaplayerservice.location";

	Queue<String> pathsToPlay = new LinkedList<String>();

	public MediaPlayerService() {
		super();
	}

	private MediaPlayer mediaPlayer;

	private final OnCompletionListener onCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(final MediaPlayer mp) {
			mp.release();
			mediaPlayer = null;
			if (pathsToPlay.isEmpty()) {
				stopSelf();
			} else {
				try {
					playPath(pathsToPlay.poll());
				} catch (final Exception e) {
					cleanup();
					uiHelper.displayError(e);
				}
			}
		}
	};

	@Override
	public IBinder onBind(final Intent intent) {
		// We don't need to bind to this service
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		onHandleIntent(intent);
		return START_STICKY;
	}

	protected void onHandleIntent(final Intent intent) {
		if (intent == null) {
			if (mediaPlayer == null) {
				stopSelf();
			}
			return;
		}
		final Object extra = intent.getExtras().get(EXTRA_ITEM);
		if (!(extra instanceof Item)) {
			return;
		}
		final Item item = (Item) extra;
		enqueueItemForPlay(item);
	}

	@Background
	void enqueueItemForPlay(final Item item) {
		pathsToPlay.add(item.getMediaUrl());
		synchronized (pathsToPlay) {
			if (mediaPlayer == null && !pathsToPlay.isEmpty()) {
				final String path = pathsToPlay.poll();
				playPath(path);
			}
		}
	}

	private void playPath(final String path) {
		mediaPlayer = new MediaPlayer();
		Log.w("MediaPlayerService", "playPath; mediaplayer=" + mediaPlayer);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			setDataSourceForPath(mediaPlayer, path);
			mediaPlayer.setOnCompletionListener(onCompletionListener);
			mediaPlayer.prepare();
			Log.w("MediaPlayerService", "starting");
			mediaPlayer.start();
		} catch (final Exception e) {
			cleanup();
			uiHelper.displayError(e);
		}
	}

	private void cleanup() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
			stopSelf();
		}
	}

	private void setDataSourceForPath(final MediaPlayer mp, final String path)
			throws IOException {
		mp.setDataSource(this, Uri.parse(path));
	}
}