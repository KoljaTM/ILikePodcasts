package de.vanmar.android.ilikepodcasts.library.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EFragment(resName = "episodes")
public class EpisodesFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public interface EpisodesFragmentListener {
		void onItemSelected(Item item);
	}

	private static final int EPISODE_LIST_LOADER = 2;
	@Bean
	UiHelper uiHelper;

	@ViewById(resName = "episodelist")
	ListView episodelist;

	private Feed feed;

	private EpisodeListAdapter adapter;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void afterViews() {
		final String[] uiBindFrom = { Item.TITLE };
		final int[] uiBindTo = { R.id.title };

		adapter = new EpisodeListAdapter(getActivity(),
				(EpisodesFragmentListener) getActivity(), uiHelper, null,
				uiBindFrom, uiBindTo, 0);
		episodelist.setAdapter(adapter);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@UiThread
	public void onFeedSelected(final Feed feed) {
		this.feed = feed;
		getLoaderManager().initLoader(EPISODE_LIST_LOADER, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		if (feed != null) {
			final CursorLoader cursorLoader = new CursorLoader(
					getActivity(),
					Uri.withAppendedPath(
							Uri.parse(getString(R.string.episodeContentProviderUri)),
							"feed/" + feed.getId()),
					EpisodeListAdapter.projection, null, null, null);
			return cursorLoader;
		}
		return null;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
