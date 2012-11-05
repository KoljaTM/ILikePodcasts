package de.vanmar.android.ilikepodcasts.library;

import de.vanmar.android.ilikepodcasts.library.bo.Item;

public interface IDownloadService {

	public interface Callback {
		void onDownloadProgress(Item item, int progress, int total);

		void onDownloadCompleted(Item itemToDownload);
	}

	void registerCallback(Callback callback);

	void unRegisterCallback(Callback callback);

	void startDownload(Item item);

}
