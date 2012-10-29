package de.vanmar.android.ilikepodcasts.library.search;

import java.util.Collections;
import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

	private ArrayAdapter<SearchResultItem> searchResultAdapter;

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

	@AfterViews
	void setupListAdapter() {
		searchResultAdapter = new ArrayAdapter<SearchResultItem>(this,
				R.layout.searchresultlist_item) {
			@Override
			public View getView(final int position, final View convertView,
					final ViewGroup parent) {
				final View view = getLayoutInflater().inflate(
						R.layout.searchresultlist_item, null);
				final SearchResultItem item = getItem(position);
				((TextView) view.findViewById(R.id.title)).setText(item
						.getCollectionName());

				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View v) {
					}
				});

				return view;
			}

		};
		searchResults.setAdapter(searchResultAdapter);
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
	void displaySearchResults(final List<SearchResultItem> searchResults) {
		searchResultAdapter.clear();
		for (final SearchResultItem feed : searchResults) {
			searchResultAdapter.add(feed);
		}
	}
}
