package de.vanmar.android.ilikepodcasts;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ViewAnimator;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

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

	private static final int CHILD_FEED_FRAGMENT = 0;
	private static final int CHILD_EPISODES_FRAGMENT = 1;

	@Bean
	RssLoader rssLoader;

	@Bean
	UiHelper uiHelper;

	@ViewById(R.id.fragment_container)
	ViewAnimator fragmentContainer;

	@FragmentById(R.id.feedsFragment)
	FeedsFragment feedsFragment;

	@FragmentById(R.id.episodesFragment)
	EpisodesFragment episodesFragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		feedsFragment.loadFeeds();
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

	@Override
	public void onFeedSelected(final Feed feed) {
		episodesFragment.onFeedSelected(feed);
		fragmentContainer.setDisplayedChild(CHILD_EPISODES_FRAGMENT);
	}

	@Override
	public void onBackPressed() {
		if (fragmentContainer.getDisplayedChild() == CHILD_FEED_FRAGMENT) {
			super.onBackPressed();
		} else {
			fragmentContainer.setDisplayedChild(CHILD_FEED_FRAGMENT);
		}
	}

	@Override
	public void onItemSelected(final Item item) {
	}
}
