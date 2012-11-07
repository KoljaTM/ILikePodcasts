package de.vanmar.android.ilikepodcasts.library;

import java.net.URL;
import java.sql.SQLException;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EActivity(resName = "feeddetail")
public class FeedDetailActivity extends FragmentActivity {

	public static final String FEED_URL = "de.vanmar.android.ilikepodcasts.library.FeedDetailActivity.feedUrl";

	@Extra(FEED_URL)
	String feedUrl;

	@Bean
	protected UiHelper uiHelper;

	@Bean
	protected RssLoader rssLoader;

	@ViewById(resName = "feedTitle")
	TextView feedTitle;

	private Feed feed;

	private ProgressDialog progressDialog;

	@AfterViews
	public void init() {
		feed = null;
		progressDialog = ProgressDialog.show(this, "",
				getString(R.string.readRssWaitMessage), true);
		loadData();
	}

	@Background
	public void loadData() {
		try {
			final URL url = new URL(feedUrl);
			feed = rssLoader.readRssFeed(url);
			displayFeed();
		} catch (final Exception e) {
			e.printStackTrace();
			uiHelper.displayError(e);
		} finally {
			progressDialog.dismiss();
		}

	}

	@UiThread
	void displayFeed() {
		feedTitle.setText(feed.getTitle());
	}

	@Click(resName = "addFeed")
	void onAddFeedClicked() {
		progressDialog = ProgressDialog.show(this, "",
				getString(R.string.saveRssWaitMessage), true);
		addFeed();
	}

	@Background
	void addFeed() {
		try {
			if (feed != null) {
				rssLoader.addFeed(feed);
			}
			finish();
		} catch (final SQLException e) {
			uiHelper.displayError(e);
		} finally {
			progressDialog.dismiss();
		}
	}

}
