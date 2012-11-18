package de.vanmar.android.ilikepodcasts.library;

import java.sql.SQLException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.fragment.EpisodesFragment;
import de.vanmar.android.ilikepodcasts.library.fragment.EpisodesFragment.EpisodesFragmentListener;
import de.vanmar.android.ilikepodcasts.library.fragment.FeedsFragment;
import de.vanmar.android.ilikepodcasts.library.fragment.FeedsFragment.FeedsFragmentListener;
import de.vanmar.android.ilikepodcasts.library.fragment.PlayerFragment;
import de.vanmar.android.ilikepodcasts.library.fragment.PlayerFragment.PlayerFragmentListener;
import de.vanmar.android.ilikepodcasts.library.fragment.PlaylistFragment.PlaylistFragmentListener;
import de.vanmar.android.ilikepodcasts.library.playlist.PlaylistManager;
import de.vanmar.android.ilikepodcasts.library.search.SearchActivity_;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EActivity(resName = "main")
@OptionsMenu(resName = "menu")
public class MainActivity extends FragmentActivity implements
		FeedsFragmentListener, EpisodesFragmentListener,
		PlaylistFragmentListener, PlayerFragmentListener,
		IMediaPlayerService.Callback, IDownloadService.Callback {

	private static final int CHILD_FEED_FRAGMENT = 0;
	private static final int CHILD_EPISODES_FRAGMENT = 1;
	private static final int CHILD_PLAYLIST_FRAGMENT = 2;

	@Bean
	protected RssLoader rssLoader;

	@Bean
	protected UiHelper uiHelper;

	@Bean
	protected PlaylistManager playlistManager;

	@ViewById(resName = "fragment_container")
	protected ViewAnimator fragmentContainer;

	@ViewById(resName = "statusBar")
	protected TextView statusBar;

	@FragmentById(resName = "feedsFragment")
	protected FeedsFragment feedsFragment;

	@FragmentById(resName = "episodesFragment")
	protected EpisodesFragment episodesFragment;

	@FragmentById(resName = "playerFragment")
	protected PlayerFragment playerFragment;

	private ServiceConnection mpServiceConnection;
	private IMediaPlayerService mpService;

	private ServiceConnection dlServiceConnection;
	private IDownloadService dlService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);

		// Bind to MediaPlayerService
		mpServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(final ComponentName name) {
				mpService.unRegisterCallback(MainActivity.this);
				mpService = null;
			}

			@Override
			public void onServiceConnected(final ComponentName name,
					final IBinder service) {
				mpService = (IMediaPlayerService) service;
				mpService.registerCallback(MainActivity.this);
				playerFragment.setPlayerStatus(mpService.getPlayerStatus());
				Log.i("INFO", "MediaplayerService bound ");
			}
		};
		final Intent mpServiceIntent = new Intent(this,
				MediaPlayerService_.class);
		bindService(mpServiceIntent, mpServiceConnection,
				Context.BIND_AUTO_CREATE);

		// Bind to DownloadService
		dlServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(final ComponentName name) {
				dlService.unRegisterCallback(MainActivity.this);
				dlService = null;
			}

			@Override
			public void onServiceConnected(final ComponentName name,
					final IBinder service) {
				dlService = (IDownloadService) service;
				dlService.registerCallback(MainActivity.this);
				playerFragment.setPlayerStatus(mpService.getPlayerStatus());
				Log.i("INFO", "DownloadService bound ");
			}
		};
		final Intent dlServiceIntent = new Intent(this, DownloadService_.class);
		bindService(dlServiceIntent, dlServiceConnection,
				Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onDestroy() {
		unbindService(mpServiceConnection);
		unbindService(dlServiceConnection);
		super.onDestroy();
	}

	@OptionsItem(resName = "refresh")
	@Background
	protected void doRefresh() {
		try {
			rssLoader.refreshFeeds();
		} catch (final Exception e) {
			uiHelper.displayError(e);
		}
	}

	@OptionsItem(resName = "playlist")
	protected void doPlaylist() {
		fragmentContainer.setDisplayedChild(CHILD_PLAYLIST_FRAGMENT);
	}

	@OptionsItem(resName = "search")
	protected void doSearch() {
		this.startActivity(new Intent(this, SearchActivity_.class));
	}

	@Override
	public void onFeedSelected(final Integer feedId) {
		episodesFragment.onFeedSelected(feedId);
		fragmentContainer.setDisplayedChild(CHILD_EPISODES_FRAGMENT);
	}

	@Override
	public void onBackPressed() {
		if (fragmentContainer.getDisplayedChild() == CHILD_FEED_FRAGMENT) {
			super.onBackPressed();
		} else if (fragmentContainer.getDisplayedChild() == CHILD_PLAYLIST_FRAGMENT) {
			fragmentContainer.setDisplayedChild(CHILD_EPISODES_FRAGMENT);
		} else {
			fragmentContainer.setDisplayedChild(CHILD_FEED_FRAGMENT);
		}
	}

	@Override
	public void onItemSelected(final Integer itemId) {
		try {
			final Item item = DatabaseManager.getInstance().getItem(itemId);
			if (item.getMediaPath() == null) {
				startDownload(item);
			} else {
				playlistManager.enqueueItem(item);
				fragmentContainer.setDisplayedChild(CHILD_PLAYLIST_FRAGMENT);
			}
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}
	}

	protected void startDownload(final Item item) {
		if (dlService != null) {
			dlService.startDownload(item);
		}
	}

	@Override
	public void onItemPlay(final Integer itemId) {
		try {
			mpService.play(DatabaseManager.getInstance().getItem(itemId));
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}
	}

	@Override
	public void playStarted(final Item item, final int totalDuration) {
		Log.i("MainActivity", "Play has started");
		playerFragment.onPlayStarted(item, totalDuration);
	}

	@Override
	public void onPlaySelected() {
		mpService.play();
	}

	@Override
	public void playPaused() {
		Log.i("MainActivity", "Play was paused");
		playerFragment.onPaused();
	}

	@Override
	public void playStopped() {
		Log.i("MainActivity", "Play was stopped");
		playerFragment.onStopped();
	}

	@Override
	public void onPauseSelected() {
		mpService.pause();
	}

	@Override
	public void onSkipBack() {
		mpService.skipBack();
	}

	@Override
	public void onSkipForward() {
		mpService.skipForward();
	}

	@Override
	public int getPlaybackPosition() {
		return mpService.getPlaybackPosition();
	}

	@Override
	public void onSeek(final int progress) {
		mpService.seekToPosition(progress);
	}

	@Override
	public int getTotalDuration() {
		return mpService.getTotalDuration();
	}

	@Override
	@UiThread
	public void onDownloadProgress(final Item item, final int progress,
			final int total) {
		statusBar.setVisibility(View.VISIBLE);
		statusBar.setText(String.format(getString(R.string.downloadStatus),
				item.getTitle(), progress, total));
	}

	@Override
	@UiThread
	public void onDownloadCompleted(final Item item) {
		statusBar.setVisibility(View.GONE);
		Toast.makeText(this,
				getString(R.string.downloadComplete, item.getTitle()),
				Toast.LENGTH_SHORT).show();
	}
}
