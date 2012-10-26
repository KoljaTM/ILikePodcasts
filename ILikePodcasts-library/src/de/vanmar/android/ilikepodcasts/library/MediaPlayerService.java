package de.vanmar.android.ilikepodcasts.library;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.playlist.PlayPosition;
import de.vanmar.android.ilikepodcasts.library.playlist.PlaylistManager;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@SuppressLint("Registered")
@EService
public class MediaPlayerService extends Service {

	@Bean
	UiHelper uiHelper;

	@Bean
	PlaylistManager playlistManager;

	public static final String EXTRA_ITEM = "de.vanmar.android.ilikepodcasts.mediaplayerservice.location";
	private MediaPlayerServiceBinder myServiceBinder = new MediaPlayerServiceBinder();

	public MediaPlayerService() {
		super();
	}

	private MediaPlayer mediaPlayer;

	private final OnCompletionListener onCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(final MediaPlayer mp) {
			mp.release();
			mediaPlayer = null;
			try {
				playing = playlistManager.getNextItem(playing);
				if (playing == null) {
					stopSelf();
				} else {
					playItem(new PlayPosition(playing, playing.getPosition()));
				}
			} catch (final SQLException e) {
				cleanup();
				uiHelper.displayError(e);
			}
		}
	};

	public Item playing;

	@Override
	public IBinder onBind(final Intent intent) {
		return myServiceBinder; // object of the class that implements Service
								// interface.
	}

	@Override
	public void onDestroy() {
		cleanup();
		super.onDestroy();
	}

	public class MediaPlayerServiceBinder extends Binder implements
			IMediaPlayerService {

		private final Set<Callback> callbacks = new HashSet<IMediaPlayerService.Callback>();

		@Override
		public void registerCallback(final Callback callback) {
			callbacks.add(callback);
		}

		@Override
		public void unRegisterCallback(final Callback callback) {
			callbacks.remove(callback);
		}

		@Override
		public void play() {
			try {
				if (mediaPlayer != null) {
					mediaPlayer.start();
				} else {
					PlayPosition playPosition;
					playPosition = playlistManager.getPlayPosition();
					Log.i("MediaPlayerService",
							"Play requested: "
									+ playPosition.getItem().getTitle()
									+ playPosition.getPosition());
					playItem(playPosition);
				}
				for (final Callback callback : callbacks) {
					callback.playStarted();
				}
			} catch (final SQLException e) {
				uiHelper.displayError(e);
			}
		}

		@Override
		public void play(final Item item) {
			Log.i("MediaPlayerService", "Play requested: " + item.getTitle());
			playItem(new PlayPosition(item, item.getPosition()));
			for (final Callback callback : callbacks) {
				callback.playStarted();
			}
		}

		@Override
		public void pause() {
			Log.i("MediaPlayerService", "Pause requested");
			pausePlayback();
			for (final Callback callback : callbacks) {
				callback.playPaused();
			}
		}

		@Override
		public void skipForward() {
			savePositionInCurrentItem();
			Item nextItem;
			try {
				nextItem = playlistManager.getNextItem(playing);
				if (nextItem != null) {
					playItem(new PlayPosition(nextItem, nextItem.getPosition()));
				}
			} catch (final SQLException e) {
				uiHelper.displayError(e);
			}
		}
	}

	private void playItem(final PlayPosition playPosition) {
		savePositionInCurrentItem();
		playing = playPosition.getItem();
		mediaPlayer = new MediaPlayer();
		Log.w("MediaPlayerService", "playPath; mediaplayer=" + mediaPlayer);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			final File SDCardRoot = Environment.getExternalStorageDirectory();
			final File file = new File(SDCardRoot, playing.getMediaPath());
			final FileInputStream inputStream = new FileInputStream(file);
			mediaPlayer.setDataSource(inputStream.getFD());
			mediaPlayer.setOnCompletionListener(onCompletionListener);
			mediaPlayer.prepare();
			Log.w("MediaPlayerService", "starting");
			mediaPlayer.seekTo(playPosition.getPosition());
			mediaPlayer.start();
		} catch (final Exception e) {
			cleanup();
			uiHelper.displayError(e);
		}
	}

	public void pausePlayback() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			savePlayPosition();
		}

	}

	private void savePlayPosition() {
		if (mediaPlayer != null && playing != null) {
			playlistManager.setLastPlayPosition(new PlayPosition(playing,
					mediaPlayer.getCurrentPosition()));
		}
	}

	@Background
	void savePositionInCurrentItem() {
		if (mediaPlayer != null && playing != null) {
			try {
				playlistManager.savePlayPosition(new PlayPosition(playing,
						mediaPlayer.getCurrentPosition()));
			} catch (final SQLException e) {
				uiHelper.displayError(e);
			}
		}
	}

	private void cleanup() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
			stopSelf();
		}
	}
}