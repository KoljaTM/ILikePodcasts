package de.vanmar.android.ilikepodcasts.library.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;

import de.vanmar.android.ilikepodcasts.library.IMediaPlayerService;
import de.vanmar.android.ilikepodcasts.library.IMediaPlayerService.Callback;
import de.vanmar.android.ilikepodcasts.library.MediaPlayerService_;

@EFragment(resName = "player")
public class PlayerFragment extends Fragment implements Callback {

	private ServiceConnection mpServiceConnection;
	private IMediaPlayerService mpService;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Bind to MediaPlayerService
		mpServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(final ComponentName name) {
				mpService.unRegisterCallback(PlayerFragment.this);
				mpService = null;
			}

			@Override
			public void onServiceConnected(final ComponentName name,
					final IBinder service) {
				mpService = (IMediaPlayerService) service;
				mpService.registerCallback(PlayerFragment.this);
				Log.i("INFO", "Service bound ");
			}
		};
		final Intent intent = new Intent(this.getActivity(),
				MediaPlayerService_.class);
		this.getActivity().bindService(intent, mpServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		this.getActivity().unbindService(mpServiceConnection);
		super.onDestroy();
	}

	@Click(resName = "playButton")
	void onSimplePlayButtonClicked() {
		if (mpService == null) {
			Log.w("PlayerFragment", "Service not connected");
		} else {
			mpService.play();
		}
	}

	@Override
	public void playStarted() {
		Log.i("PlayerFragment", "Play has started");
	}

}
