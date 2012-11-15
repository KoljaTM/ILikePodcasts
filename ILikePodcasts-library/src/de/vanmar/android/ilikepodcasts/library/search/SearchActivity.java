package de.vanmar.android.ilikepodcasts.library.search;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.util.NetworkHelper;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EActivity(resName = "search")
public class SearchActivity extends FragmentActivity {

	@ViewById(resName = "searchButton")
	Button simpleSearchButton;

	@ViewById(resName = "searchBox")
	EditText searchBox;

	@ViewById(resName = "searchResults")
	ListView searchResults;

	@SystemService
	InputMethodManager inputMethodManager;

	@Bean
	NetworkHelper networkHelper;

	@Bean
	UiHelper uiHelper;

	@Bean
	SearchHelper searchHelper;

	@AfterViews
	void setupListeners() {
		searchBox.setOnEditorActionListener(new OnEditorActionListener() {
			// enter key in search box triggers search
			@Override
			public boolean onEditorAction(final TextView v, final int actionId,
					final KeyEvent event) {
				if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
					return false;
				}
				if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null
						|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					onSearchButtonClicked();
				}
				return true;
			}
		});
	}

	@Click(resName = "searchButton")
	void onSearchButtonClicked() {
		if (networkHelper.networkAvailable()) {
			displaySearchResults(Collections.<SearchResultItem> emptyList());
			setSearchButtonActive(false);
			executeSearch(searchBox.getText().toString());
			inputMethodManager.hideSoftInputFromWindow(
					searchBox.getWindowToken(), 0);
		} else {
			Toast.makeText(this, R.string.networkNotAvailable,
					Toast.LENGTH_LONG).show();
		}
	}

	@UiThread
	void setSearchButtonActive(final boolean active) {
		simpleSearchButton.setEnabled(active);
		simpleSearchButton.setText(active ? R.string.searchButton
				: R.string.searchButtonWaiting);
	}

	@Background
	void executeSearch(final String searchText) {
		try {
			final List<SearchResultItem> searchResults = searchHelper
					.searchPodcasts(this, searchText);
			displaySearchResults(searchResults);
		} catch (final Exception e) {
			uiHelper.displayError(e);
		}
		setSearchButtonActive(true);
	}

	@UiThread
	void displaySearchResults(final List<SearchResultItem> searchResultList) {
		searchResults.setAdapter(new SearchListAdapter(this, searchResultList));
	}

	@Background
	void setImage(final ImageView imageView, final SearchResultItem item) {
		Drawable drawable = null;
		imageView.setTag(item);
		imageView.setImageDrawable(null);
		if (item.getImage() != null) {
			// Drawable already loaded
			drawable = item.getImage();
			Log.i("Image already found",
					imageView.toString() + item.getCollectionName());
		} else if (item.getArtworkUrl60() != null) {
			try {
				Log.i("SearchActivity",
						item.getCollectionName() + item.getArtworkUrl60());
				final Drawable drawableFromUrl = Drawable.createFromStream(
						((InputStream) new URL(item.getArtworkUrl60())
								.getContent()), getString(R.string.imageDesc));
				item.setImage(drawableFromUrl);
				Log.i("Image set",
						imageView.toString() + item.getCollectionName());
				drawable = drawableFromUrl;
			} catch (final Exception e) {
				Log.w("SearchActivity", "Error loading drawable from " + item,
						e);
				item.setArtworkUrl60(null);
			}
		}
		setDrawable(imageView, drawable, item);
	}

	@UiThread
	void setDrawable(final ImageView imageView, final Drawable drawable,
			final SearchResultItem item) {
		if (item.equals(imageView.getTag())) {
			// only set image if Tag is still valid
			imageView.setImageDrawable(drawable);
		}
	}
}
