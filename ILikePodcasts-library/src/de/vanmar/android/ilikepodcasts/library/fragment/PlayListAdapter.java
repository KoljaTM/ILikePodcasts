package de.vanmar.android.ilikepodcasts.library.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.fragment.PlaylistFragment.PlaylistFragmentListener;

public class PlayListAdapter extends SimpleCursorAdapter {
	public static final String[] projection = new String[] { Item.ID,
			Item.TITLE, Item.MEDIA_PATH };

	private final PlaylistFragmentListener handler;

	private static class ViewHolder {
		public TextView play;
	}

	protected PlayListAdapter(final Activity context,
			final PlaylistFragmentListener handler, final Cursor cursor,
			final String[] from, final int[] to, final int flags) {
		super(context, R.layout.playlistitem, cursor, from, to, flags);
		this.handler = handler;
	}

	@Override
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		if (view.getTag() == null) {
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.play = (TextView) view.findViewById(R.id.play);
			view.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) view.getTag();
		final String mediaPath = getCursor().getString(2);
		if (mediaPath == null) {
			holder.play.setVisibility(View.GONE);
		} else {
			holder.play.setVisibility(View.VISIBLE);
		}

		final int itemId = getCursor().getInt(0);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				handler.onItemPlay(itemId);
			}
		});

		return view;
	}
}
