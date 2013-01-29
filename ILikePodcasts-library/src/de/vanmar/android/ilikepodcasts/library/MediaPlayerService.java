package de.vanmar.android.ilikepodcasts.library;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.SystemService;

import de.vanmar.android.ilikepodcasts.library.IMediaPlayerService.Callback;
import de.vanmar.android.ilikepodcasts.library.PlayerStatus.PlayerState;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.playlist.PlaylistManager;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@SuppressLint("Registered")
@EService
public class MediaPlayerService extends Service {

	@Bean
	UiHelper uiHelper;

	@Bean
	PlaylistManager playlistManager;

	@SystemService
	AudioManager audioManager;

	public static final String EXTRA_ITEM = "de.vanmar.android.ilikepodcasts.mediaplayerservice.location";

	private static final int MEDIAPLAYER_NOTIFICATION = 17;
	private static final String LOG_TAG = "MediaPlayerService";

	private final MediaPlayerServiceBinder myServiceBinder = new MediaPlayerServiceBinder();

	private MediaPlayer mediaPlayer;

	private final OnAudioFocusChangeListener onAudioFocusChangeListener = new OnAudioFocusChangeListener() {

		@Override
		public void onAudioFocusChange(final int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				// Pause playback
				pausePlayback();
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				// Resume playback
				play();
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				// Stop playback
				pausePlayback();
			}
		}
	};

	private final OnCompletionListener onCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(final MediaPlayer mp) {
			doOnItemCompleted();
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

	private void inForeground(final Item item) {
		final Intent notificationIntent = new Intent(this, MainActivity_.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		final Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.logo)
				.setTicker(getText(R.string.nowPlayingTitle))
				.setContentTitle(getText(R.string.nowPlayingNotificationTitle))
				.setContentText(
						getString(R.string.nowPlayingNotificationText,
								item.getTitle()))
				.setContentIntent(pendingIntent).getNotification();
		startForeground(MEDIAPLAYER_NOTIFICATION, notification);
	}

	private void stopForeground() {
		stopForeground(true);
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
			MediaPlayerService.this.play();
		}

		@Override
		public void play(final Item item) {
			Log.i(LOG_TAG, "Play requested: " + item.getTitle());
			playItem(item);
		}

		@Override
		public void pause() {
			Log.i(LOG_TAG, "Pause requested");
			pausePlayback();
			for (final Callback callback : callbacks) {
				callback.playPaused();
			}
		}

		@Override
		public void skipBack() {
			savePositionInCurrentItem();
			Item prevItem;
			try {
				prevItem = playlistManager.getPreviousItem(playing);
				if (prevItem != null) {
					playItem(prevItem);
				}
			} catch (final SQLException e) {
				uiHelper.displayError(e);
			}
		}

		@Override
		public void skipForward() {
			savePositionInCurrentItem();
			Item nextItem;
			try {
				nextItem = playlistManager.getNextItem(playing, false);
				if (nextItem != null) {
					playItem(nextItem);
				}
			} catch (final SQLException e) {
				uiHelper.displayError(e);
			}
		}

		@Override
		public int getPlaybackPosition() {
			if (mediaPlayer == null) {
				return 0;
			} else {
				return mediaPlayer.getCurrentPosition();
			}
		}

		@Override
		public void seekToPosition(final int position) {
			if (mediaPlayer != null) {
				mediaPlayer.seekTo(position);
				startPlay();
			}
		}

		@Override
		public int getTotalDuration() {
			if (mediaPlayer == null) {
				return 0;
			} else {
				return mediaPlayer.getDuration();
			}
		}

		@Override
		public PlayerStatus getPlayerStatus() {
			final PlayerStatus playerStatus = new PlayerStatus();
			if (mediaPlayer == null) {
				playerStatus.setState(PlayerState.STOPPED);
			} else {
				playerStatus.setState(PlayerState.STARTED);
				playerStatus.setTotalDuration(mediaPlayer.getDuration());
				playerStatus.setProgress(mediaPlayer.getCurrentPosition());
				playerStatus.setItem(playing);
			}
			return playerStatus;
		}
	}

	private void play() {
		try {
			if (mediaPlayer != null) {
				startPlay();
			} else {
				final Item playPosition = playlistManager.getPlayPosition();
				if (playPosition != null) {
					Log.i(LOG_TAG, "Play requested: " + playPosition.getTitle()
							+ playPosition.getPosition());
					playItem(playPosition);
				}
			}
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}
	}

	private void playItem(final Item item) {
		savePositionInCurrentItem();
		playing = item;
		mediaPlayer = getMediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			final File SDCardRoot = Environment.getExternalStorageDirectory();
			final File file = new File(SDCardRoot, playing.getMediaPath());
			if (!file.exists()) {
				doOnFileNotExisting();
				return;
			}
			final FileInputStream inputStream = new FileInputStream(file);
			mediaPlayer.setDataSource(inputStream.getFD());
			mediaPlayer.prepare();
			mediaPlayer.seekTo(item.getPosition());
			startPlay();
		} catch (final Exception e) {
			cleanup();
			uiHelper.displayError(e);
		}
	}

	private void doOnFileNotExisting() {
		doOnItemCompleted();
	}

	private void doOnItemCompleted() {
		mediaPlayer.release();
		mediaPlayer = null;
		try {
			playing = playlistManager.getNextItem(playing, true);
			if (playing == null) {
				savePlayPosition();
				stopForeground();
				for (final Callback callback : myServiceBinder.callbacks) {
					callback.playStopped();
				}
				stopSelf();
			} else {
				playItem(playing);
			}
		} catch (final SQLException e) {
			cleanup();
			uiHelper.displayError(e);
		}
	}

	private void startPlay() {
		if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager
				.requestAudioFocus(onAudioFocusChangeListener,
						AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
			mediaPlayer.start();
			inForeground(playing);
			for (final Callback callback : myServiceBinder.callbacks) {
				callback.playStarted(playing, mediaPlayer.getDuration());
			}
		}
	}

	private MediaPlayer getMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
		mediaPlayer = createMediaPlayer();
		mediaPlayer.setOnCompletionListener(onCompletionListener);
		return mediaPlayer;
	}

	protected MediaPlayer createMediaPlayer() {
		return new MediaPlayer();
	}

	private void pausePlayback() {
		stopForeground();
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			savePlayPosition();
		}

	}

	private void savePlayPosition() {
		if (mediaPlayer != null && playing != null) {
			playlistManager.setLastPlayPosition(playing,
					mediaPlayer.getCurrentPosition());
		} else {
			playlistManager.setLastPlayPosition(null, 0);
		}
	}

	private void savePositionInCurrentItem() {
		if (mediaPlayer != null && playing != null) {
			savePositionInCurrentItemInBackground(playing,
					mediaPlayer.getCurrentPosition());
		}
	}

	@Background
	void savePositionInCurrentItemInBackground(final Item item,
			final int position) {
		try {
			playlistManager.savePlayPosition(item, position);
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}
	}

	private void cleanup() {
		audioManager.abandonAudioFocus(onAudioFocusChangeListener);
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
			stopSelf();
		}
	}
}
