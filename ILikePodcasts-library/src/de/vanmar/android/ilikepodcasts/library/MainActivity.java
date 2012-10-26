package de.vanmar.android.ilikepodcasts.library;

import java.sql.SQLException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ViewAnimator;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.library.IMediaPlayerService.Callback;
import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.fragment.EpisodesFragment;
import de.vanmar.android.ilikepodcasts.library.fragment.EpisodesFragment.EpisodesFragmentListener;
import de.vanmar.android.ilikepodcasts.library.fragment.FeedsFragment;
import de.vanmar.android.ilikepodcasts.library.fragment.FeedsFragment.FeedsFragmentListener;
import de.vanmar.android.ilikepodcasts.library.fragment.PlayerFragment.PlayerFragmentListener;
import de.vanmar.android.ilikepodcasts.library.fragment.PlaylistFragment.PlaylistFragmentListener;
import de.vanmar.android.ilikepodcasts.library.playlist.PlaylistManager;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EActivity(resName = "main")
@OptionsMenu(resName = "menu")
public class MainActivity extends FragmentActivity implements
		FeedsFragmentListener, EpisodesFragmentListener,
		PlaylistFragmentListener, PlayerFragmentListener, Callback {

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

	@FragmentById(resName = "feedsFragment")
	protected FeedsFragment feedsFragment;

	@FragmentById(resName = "episodesFragment")
	protected EpisodesFragment episodesFragment;

	private ServiceConnection mpServiceConnection;
	private IMediaPlayerService mpService;

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
				Log.i("INFO", "Service bound ");
			}
		};
		final Intent intent = new Intent(this, MediaPlayerService_.class);
		bindService(intent, mpServiceConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		unbindService(mpServiceConnection);
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

	@Override
	public void onFeedSelected(final Feed feed) {
		episodesFragment.onFeedSelected(feed);
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
	public void onItemSelected(final Item item) {
		if (item.getMediaPath() == null) {
			startDownload(item);
		} else {
			try {
				playlistManager.enqueueItem(item);
				fragmentContainer.setDisplayedChild(CHILD_PLAYLIST_FRAGMENT);
			} catch (final SQLException e) {
				uiHelper.displayError(e);
			}
		}
	}

	protected void startDownload(final Item item) {
		final Intent intent = new Intent(this, DownloadService_.class);
		intent.putExtra(DownloadService.EXTRA_ITEM, item);
		intent.putExtra("receiver", new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(final int resultCode,
					final Bundle resultData) {
				super.onReceiveResult(resultCode, resultData);
				if (resultCode == DownloadService.UPDATE_PROGRESS) {
					final int progress = resultData.getInt("progress");
					// holder.progress.setText(String.valueOf(progress));
					if (progress == 100) {
						// // onFeedSelected(feed);
					}
				}
			}
		});
		startService(intent);
	}

	@Override
	public void onItemPlay(final Item item) {
		mpService.play(item);
	}

	@Override
	public void playStarted() {
		Log.i("MainActivity", "Play has started");
	}

	@Override
	public void onPlaySelected() {
		mpService.play();
	}
}
