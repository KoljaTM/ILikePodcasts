package de.vanmar.android.ilikepodcasts;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;

import de.vanmar.android.ilikepodcasts.bo.Feed;
import de.vanmar.android.ilikepodcasts.bo.Item;
import de.vanmar.android.ilikepodcasts.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.fragment.EpisodesFragment;
import de.vanmar.android.ilikepodcasts.fragment.EpisodesFragment.EpisodesFragmentListener;
import de.vanmar.android.ilikepodcasts.fragment.FeedsFragment;
import de.vanmar.android.ilikepodcasts.fragment.FeedsFragment.FeedsFragmentListener;
import de.vanmar.android.ilikepodcasts.util.UiHelper;

@EActivity(R.layout.main)
@OptionsMenu(R.menu.menu)
public class MainActivity extends FragmentActivity implements
		FeedsFragmentListener, EpisodesFragmentListener {

	@Bean
	RssLoader rssLoader;

	@Bean
	UiHelper uiHelper;

	@FragmentById(R.id.feedsFragment)
	FeedsFragment feedsFragment;

	@FragmentById(R.id.episodesFragment)
	EpisodesFragment episodesFragment;

	@Click(R.id.startButton)
	void onButtonClicked() {
		feedsFragment.loadFeeds();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@OptionsItem(R.id.refresh)
	@Background
	void doRefresh() {
		try {
			rssLoader.refreshFeeds();
		} catch (final Exception e) {
			uiHelper.displayError(e);
		}
		feedsFragment.loadFeeds();
	}

	@Background
	void loadRss() {
		try {
			// final Feed feed = rssLoader.loadRss();
		} catch (final Exception e) {
			uiHelper.displayError(e);
		}
	}

	@Override
	public void onFeedSelected(final Feed feed) {
		episodesFragment.onFeedSelected(feed);
	}

	@Override
	public void onItemSelected(final Item item) {
	}
}
