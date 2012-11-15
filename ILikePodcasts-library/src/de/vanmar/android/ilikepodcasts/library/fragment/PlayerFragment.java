package de.vanmar.android.ilikepodcasts.library.fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.ilikepodcasts.library.PlayerStatus;
import de.vanmar.android.ilikepodcasts.library.PlayerStatus.PlayerState;
import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Item;

@EFragment(resName = "player")
public class PlayerFragment extends Fragment {
	private PlayerFragmentListener listener;

	private static final int HOUR = 1000 * 60 * 60;

	private static final String DURATION_FORMAT = "mm:ss";
	private static final String DURATION_FORMAT_WITH_HOUR = "h:mm:ss";

	@ViewById(resName = "playButton")
	ImageButton playButton;

	@ViewById(resName = "pauseButton")
	ImageButton pauseButton;

	@ViewById(resName = "title")
	TextView title;

	@ViewById(resName = "time")
	TextView time;

	@ViewById(resName = "position")
	SeekBar position;

	private boolean playing = false;
	private int totalDuration = 0;

	public interface PlayerFragmentListener {
		void onPlaySelected();

		void onPauseSelected();

		void onSkipForward();

		void onSkipBack();

		int getPlaybackPosition();

		void onSeek(int progress);

		int getTotalDuration();
	}

	@AfterViews
	void afterViews() {
		listener = (PlayerFragmentListener) getActivity();
		position.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				listener.onSeek(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				listener.onPauseSelected();
			}

			@Override
			public void onProgressChanged(final SeekBar seekBar,
					final int progress, final boolean fromUser) {
				time.setText(formatTime(progress, totalDuration));
			}
		});
		// TODO: find good icon for SeekBar
		position.setThumb(getActivity().getResources().getDrawable(
				R.drawable.logo));
		initProgress(totalDuration);
	}

	@Click(resName = "playButton")
	void onPlayButtonClicked() {
		listener.onPlaySelected();
	}

	public void onPlayStarted(final Item item, final int totalDuration) {
		playButton.setVisibility(View.GONE);
		pauseButton.setVisibility(View.VISIBLE);
		title.setText(item.getTitle());
		this.playing = true;
		this.totalDuration = totalDuration;
		initProgress(totalDuration);
		getProgress();
	}

	private void initProgress(final int totalDuration) {
		this.position.setEnabled(totalDuration != 0);
		this.position.setMax(totalDuration);
	}

	@UiThread(delay = 50)
	void getProgress() {
		final int progress = listener.getPlaybackPosition();
		this.position.setProgress(progress);
		// time.setText(formatTime(progress, totalDuration));
		if (playing) {
			getProgress();
		}
	}

	private CharSequence formatTime(final int progress, final int total) {
		final CharSequence progressString = durationString(progress);
		final CharSequence totalString = durationString(total);
		return String.format(getActivity().getString(R.string.playPosition),
				progressString, totalString);
	}

	private CharSequence durationString(final int duration) {
		final Calendar date = new GregorianCalendar();
		date.clear();
		date.add(Calendar.MILLISECOND, duration);
		if (duration < HOUR) {
			return DateFormat.format(DURATION_FORMAT, date);
		} else {
			return DateFormat.format(DURATION_FORMAT_WITH_HOUR, date);
		}
	}

	@Click(resName = "pauseButton")
	void onPauseButtonClicked() {
		listener.onPauseSelected();
	}

	@Click(resName = "previousButton")
	void onPreviousButtonClicked() {
		listener.onSkipBack();
	}

	@Click(resName = "nextButton")
	void onNextButtonClicked() {
		listener.onSkipForward();
	}

	public void onPaused() {
		playButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
		this.playing = false;
	}

	public void onStopped() {
		playButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
		this.playing = false;
		title.setText(null);
		time.setText(null);
	}

	public void setPlayerStatus(final PlayerStatus playerStatus) {
		if (playerStatus.getState() == PlayerState.STARTED) {
			onPlayStarted(playerStatus.getItem(),
					playerStatus.getTotalDuration());
		} else {
			onPaused();
		}
	}
}
