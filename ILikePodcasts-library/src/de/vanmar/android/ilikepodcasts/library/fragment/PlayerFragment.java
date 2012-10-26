package de.vanmar.android.ilikepodcasts.library.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;

@EFragment(resName = "player")
public class PlayerFragment extends Fragment {
	private PlayerFragmentListener listener;

	@ViewById(resName = "playButton")
	ImageButton playButton;

	@ViewById(resName = "pauseButton")
	ImageButton pauseButton;

	public interface PlayerFragmentListener {
		void onPlaySelected();

		void onPauseSelected();

		void onSkipForward();
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
	void onPlayButtonClicked() {
		listener.onPlaySelected();
	}

	public void onPlayStarted() {
		playButton.setVisibility(View.GONE);
		pauseButton.setVisibility(View.VISIBLE);
	}

	@Click(resName = "pauseButton")
	void onPauseButtonClicked() {
		listener.onPauseSelected();
	}

	@Click(resName = "nextButton")
	void onNxtButtonClicked() {
		listener.onSkipForward();
	}

	public void onPaused() {
		playButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
	}
}
