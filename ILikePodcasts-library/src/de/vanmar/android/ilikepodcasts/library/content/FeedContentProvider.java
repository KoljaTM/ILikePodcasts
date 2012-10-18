package de.vanmar.android.ilikepodcasts.library.content;

public class FeedContentProvider extends AbstractFeedContentProvider {

	private static final String AUTHORITY = "de.vanmar.android.ilikepodcasts.FeedContentProvider";

	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}

}
