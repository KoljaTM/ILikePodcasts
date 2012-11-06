package de.vanmar.android.ilikepodcasts.library.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.fragment.FeedsFragment.FeedsFragmentListener;

public class FeedsListAdapter extends SimpleCursorAdapter {
	public static final String[] projection = new String[] { Feed.ID,
			Feed.TITLE };

	private FeedsFragmentListener handler;

	protected FeedsListAdapter(final Activity context,
			final FeedsFragmentListener handler, final Cursor cursor,
			final String[] from, final int[] to, final int flags) {
		super(context, R.layout.feeditem, cursor, from, to, flags);
		this.handler = handler;
	}

	@Override
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		final int feedId = getCursor().getInt(0);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				handler.onFeedSelected(feedId);
			}
		});

		return view;
	}
}