package de.vanmar.android.ilikepodcasts.library.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;

@EFragment(resName = "player")
public class PlayerFragment extends Fragment {
	private PlayerFragmentListener listener;

	public interface PlayerFragmentListener {
		void onPlaySelected();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@AfterViews
	void afterViews() {
		listener = (PlayerFragmentListener) getActivity();
	}

	@Click(resName = "playButton")
	void onSimplePlayButtonClicked() {
		listener.onPlaySelected();
	}
}
