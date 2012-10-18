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
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EFragment(resName = "playlist")
public class PlaylistFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public interface PlaylistFragmentListener {
		void onItemPlay(Item item);
	}

	private static final int PLAYLIST_LOADER = 3;
	@Bean
	UiHelper uiHelper;

	@ViewById(resName = "playlist")
	ListView playlist;

	private PlayListAdapter adapter;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void afterViews() {
		final String[] uiBindFrom = { Item.TITLE };
		final int[] uiBindTo = { R.id.title };

		adapter = new PlayListAdapter(getActivity(),
				(PlaylistFragmentListener) getActivity(), uiHelper, null,
				uiBindFrom, uiBindTo, 0);
		playlist.setAdapter(adapter);
		getLoaderManager().initLoader(PLAYLIST_LOADER, null, this);
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
				Uri.withAppendedPath(Uri
						.parse(getString(R.string.episodeContentProviderUri)),
						"playlist"), EpisodeListAdapter.projection, null, null,
				null);
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
