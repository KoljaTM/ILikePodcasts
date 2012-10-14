package de.vanmar.android.ilikepodcasts.fragment;

import java.sql.SQLException;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.vanmar.android.ilikepodcasts.R;
import de.vanmar.android.ilikepodcasts.bo.Feed;
import de.vanmar.android.ilikepodcasts.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.fragment.FeedsFragment.FeedsFragmentListener;
import de.vanmar.android.ilikepodcasts.util.UiHelper;

public class FeedsListAdapter extends SimpleCursorAdapter {
	public static final String[] projection = new String[] { Feed.ID,
			Feed.TITLE };

	private FeedsFragmentListener handler;

	private DatabaseManager dbManager;
	private final UiHelper uiHelper;

	protected FeedsListAdapter(final Activity context,
			final FeedsFragmentListener handler, final UiHelper uiHelper,
			final Cursor cursor, final String[] from, final int[] to,
			final int flags) {
		super(context, R.layout.feeditem, cursor, from, to, flags);
		this.handler = handler;
		this.uiHelper = uiHelper;
		this.dbManager = DatabaseManager.getInstance();
	}

	@Override
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		final int feedId = getCursor().getInt(0);
		try {
			final Feed feed = dbManager.getFeed(feedId);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					handler.onFeedSelected(feed);
				}
			});
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}

		return view;
	}
}