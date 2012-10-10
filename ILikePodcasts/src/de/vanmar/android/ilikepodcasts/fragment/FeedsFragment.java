package de.vanmar.android.ilikepodcasts.fragment;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.R;
import de.vanmar.android.ilikepodcasts.bo.Feed;
import de.vanmar.android.ilikepodcasts.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.util.UiHelper;

@EFragment(R.layout.feeds)
public class FeedsFragment extends Fragment {

	public interface FeedsFragmentListener {
		void onFeedSelected(Feed feed);
	}

	@Bean
	UiHelper uiHelper;

	private final class FeedListAdapter extends ArrayAdapter<Feed> {
		private final Activity context;
		private final List<Feed> feeds;

		private FeedListAdapter(final Activity context,
				final int textViewResourceId, final List<Feed> feeds) {
			super(context, textViewResourceId, feeds);
			this.context = context;
			this.feeds = feeds;
		}

		class ViewHolder {
			public TextView title;
		}

		@Override
		public final View getView(final int position, final View convertView,
				final ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				final LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.feeditem, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.title = (TextView) rowView.findViewById(R.id.title);
				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();
			final Feed feed = feeds.get(position);
			holder.title.setText(feed.getTitle());

			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					handler.onFeedSelected(feed);
				}
			});

			return rowView;
		}
	}

	@ViewById(R.id.feedlist)
	ListView feedlist;

	private FeedsFragmentListener handler;

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		try {
			this.handler = (FeedsFragmentListener) activity;
		} catch (final ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement FeedsFragmentListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		loadFeeds();
	}

	@Background
	public void loadFeeds() {
		List<Feed> feeds;
		try {
			feeds = DatabaseManager.getInstance().getAllFeeds();
			displayFeeds(feeds);
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}
	}

	@UiThread
	void displayFeeds(final List<Feed> feeds) {
		feedlist.setAdapter(new FeedListAdapter(getActivity(), R.id.feedlist,
				feeds));
	}
}
