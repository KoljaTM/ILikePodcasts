package de.vanmar.android.ilikepodcasts.library.content;

public class EpisodeContentProvider extends AbstractEpisodeContentProvider {

	private static final String AUTHORITY = "de.vanmar.android.ilikepodcasts.EpisodeContentProvider";

	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}

}
