package de.vanmar.android.ilikepodcasts.fragment;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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

import de.vanmar.android.ilikepodcasts.DownloadService;
import de.vanmar.android.ilikepodcasts.DownloadService_;
import de.vanmar.android.ilikepodcasts.R;
import de.vanmar.android.ilikepodcasts.bo.Feed;
import de.vanmar.android.ilikepodcasts.bo.Item;
import de.vanmar.android.ilikepodcasts.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.util.UiHelper;

@EFragment(R.layout.episodes)
public class EpisodesFragment extends Fragment {

	public interface EpisodesFragmentListener {
		void onItemSelected(Item item);
	}

	@Bean
	UiHelper uiHelper;

	private static class ViewHolder {
		public TextView title;
		public TextView progress;
	}

	private final class EpisodeListAdapter extends ArrayAdapter<Item> {
		private final Activity context;
		private final List<Item> items;

		private EpisodeListAdapter(final Activity context,
				final int textViewResourceId, final List<Item> items) {
			super(context, textViewResourceId, items);
			this.context = context;
			this.items = items;
		}

		@Override
		public final View getView(final int position, final View convertView,
				final ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				final LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.episodesitem, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.title = (TextView) rowView.findViewById(R.id.title);
				viewHolder.progress = (TextView) rowView
						.findViewById(R.id.progress);
				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();
			final Item item = items.get(position);
			holder.title.setText(item.getTitle());

			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					startDownload(item, holder);
				}
			});

			return rowView;
		}
	}

	@ViewById(R.id.episodelist)
	ListView episodelist;

	private EpisodesFragmentListener handler;

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		try {
			this.handler = (EpisodesFragmentListener) activity;
		} catch (final ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement EpisodesFragmentListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Background
	public void onFeedSelected(final Feed feed) {
		try {
			final List<Item> items = DatabaseManager.getInstance()
					.getAllItemsOrdered(feed.getId());
			displayEpisodes(items);
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		}
	}

	@UiThread
	void displayEpisodes(final List<Item> items) {
		episodelist.setAdapter(new EpisodeListAdapter(getActivity(),
				R.id.episodelist, items));
	}

	protected void startDownload(final Item item, final ViewHolder holder) {
		final Intent intent = new Intent(this.getActivity(),
				DownloadService_.class);
		intent.putExtra("url", item.getMediaUrl());
		intent.putExtra("receiver", new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(final int resultCode,
					final Bundle resultData) {
				super.onReceiveResult(resultCode, resultData);
				if (resultCode == DownloadService.UPDATE_PROGRESS) {
					final int progress = resultData.getInt("progress");
					holder.progress.setText(String.valueOf(progress));
				}
			}
		});
		this.getActivity().startService(intent);
	}

}
