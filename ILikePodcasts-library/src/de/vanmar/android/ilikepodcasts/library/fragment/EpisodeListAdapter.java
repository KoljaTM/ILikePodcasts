package de.vanmar.android.ilikepodcasts.library.fragment;

import java.sql.SQLException;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.fragment.EpisodesFragment.EpisodesFragmentListener;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

public class EpisodeListAdapter extends SimpleCursorAdapter {
	public static final String[] projection = new String[] { Item.ID,
			Item.TITLE, Item.MEDIA_PATH };

	private EpisodesFragmentListener handler;
	private UiHelper uiHelper;
	private DatabaseManager dbManager;

	private static class ViewHolder {
		public TextView download;
		public TextView play;
	}

	protected EpisodeListAdapter(final Activity context,
			final EpisodesFragmentListener handler, final UiHelper uiHelper,
			final Cursor cursor, final String[] from, final int[] to,
			final int flags) {
		super(context, R.layout.episodesitem, cursor, from, to, flags);
		this.handler = handler;
		this.uiHelper = uiHelper;
		this.dbManager = DatabaseManager.getInstance();
	}

	@Override
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		if (view.getTag() == null) {
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.download = (TextView) view.findViewById(R.id.download);
			viewHolder.play = (TextView) view.findViewById(R.id.play);
			view.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) view.getTag();
		final String mediaPath = getCursor().getString(2);
		if (mediaPath == null) {
			holder.download.setVisibility(View.VISIBLE);
			holder.play.setVisibility(View.GONE);
		} else {
			holder.download.setVisibility(View.GONE);
			holder.play.setVisibility(View.VISIBLE);
		}

		final int itemId = getCursor().getInt(0);
		try {
			final Item item = dbManager.getItem(itemId);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					handler.onItemSelected(item);
				}
			});
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}

		return view;
	}
}