package de.vanmar.android.ilikepodcasts.library.search;

import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.vanmar.android.ilikepodcasts.library.FeedDetailActivity;
import de.vanmar.android.ilikepodcasts.library.FeedDetailActivity_;
import de.vanmar.android.ilikepodcasts.library.R;

final class SearchListAdapter extends ArrayAdapter<SearchResultItem> {

	private final SearchActivity activity;

	SearchListAdapter(final SearchActivity activity,
			final List<SearchResultItem> searchResultList) {
		super(activity, R.layout.searchresultlist_item, searchResultList);
		this.activity = activity;
	}

	private static class ViewHolder {
		public TextView artist;
		public TextView feedName;
		public TextView url;
		public ImageView image;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {

		View view = convertView;

		final ViewHolder viewHolder;
		if (view == null) {
			view = activity.getLayoutInflater().inflate(
					R.layout.searchresultlist_item, null);
			viewHolder = new ViewHolder();
			viewHolder.artist = (TextView) view.findViewById(R.id.artist);
			viewHolder.feedName = (TextView) view.findViewById(R.id.feedName);
			viewHolder.url = (TextView) view.findViewById(R.id.url);
			viewHolder.image = (ImageView) view.findViewById(R.id.image);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		final SearchResultItem item = getItem(position);
		viewHolder.artist.setText(item.getArtistName());
		viewHolder.feedName.setText(item.getCollectionName());
		viewHolder.url.setText(item.getFeedUrl());
		activity.setImage(viewHolder.image, item);
		Log.i("Set Image ",
				convertView == null ? "view: " + view.toString()
						: "convertView: " + convertView.toString()
								+ item.getCollectionName());

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent intent = new Intent(activity,
						FeedDetailActivity_.class);
				intent.putExtra(FeedDetailActivity.FEED_URL, item.getFeedUrl());
				activity.startActivity(intent);
			}
		});

		return view;
	}
}
