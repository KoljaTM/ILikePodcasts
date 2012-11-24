package de.vanmar.android.ilikepodcasts.library.fragment;

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
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EFragment(resName = "episodes")
public class EpisodesFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public interface EpisodesFragmentListener {
		void onItemSelected(Integer itemId);
	}

	private static final int EPISODE_LIST_LOADER = 2;
	@Bean
	UiHelper uiHelper;

	@ViewById(resName = "episodelist")
	ListView episodelist;

	private Integer feedId;

	private EpisodeListAdapter adapter;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(EPISODE_LIST_LOADER, null, this);
	}

	@AfterViews
	void afterViews() {
		final String[] uiBindFrom = { Item.TITLE };
		final int[] uiBindTo = { R.id.title };

		adapter = new EpisodeListAdapter(getActivity(),
				(EpisodesFragmentListener) getActivity(), null, uiBindFrom,
				uiBindTo, 0);
		episodelist.setAdapter(adapter);
	}

	@UiThread
	public void onFeedSelected(final Integer feedId) {
		this.feedId = feedId;
		getLoaderManager().restartLoader(EPISODE_LIST_LOADER, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		if (feedId != null) {
			return new CursorLoader(getActivity(), Uri.withAppendedPath(
					Uri.parse(getString(R.string.episodeContentProviderUri)),
					"feed/" + feedId), EpisodeListAdapter.projection, null,
					null, null);
		} else {
			return new CursorLoader(getActivity(),
					Uri.parse(getString(R.string.episodeContentProviderUri)),
					EpisodeListAdapter.projection, null, null, null);
		}
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
