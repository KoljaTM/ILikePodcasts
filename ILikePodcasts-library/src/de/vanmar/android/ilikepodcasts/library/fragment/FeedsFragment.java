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
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EFragment(resName = "feeds")
public class FeedsFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public interface FeedsFragmentListener {
		void onFeedSelected(Feed feed);
	}

	private static final int FEED_LIST_LOADER = 1;

	@Bean
	UiHelper uiHelper;

	@ViewById(resName = "feedlist")
	ListView feedlist;

	private FeedsListAdapter adapter;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void afterViews() {
		final String[] uiBindFrom = { Feed.TITLE };
		final int[] uiBindTo = { R.id.title };
		getLoaderManager().initLoader(FEED_LIST_LOADER, null, this);

		adapter = new FeedsListAdapter(getActivity(),
				(FeedsFragmentListener) getActivity(), uiHelper, null,
				uiBindFrom, uiBindTo, 0);
		feedlist.setAdapter(adapter);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		final CursorLoader cursorLoader = new CursorLoader(getActivity(),
				Uri.parse(getString(R.string.feedContentProviderUri)),
				FeedsListAdapter.projection, null, null, null);
		return cursorLoader;
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
