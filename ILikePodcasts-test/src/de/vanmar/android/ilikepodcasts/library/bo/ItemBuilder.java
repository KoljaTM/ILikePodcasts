package de.vanmar.android.ilikepodcasts.library.bo;

import java.util.Date;

public class ItemBuilder {

	private int position;
	private String mediaPath = "mediaPath";
	private String description = "description";
	private String url = "url";
	private Integer mediaLength;
	private String title = "title";
	private Date published;
	private int id;
	private String mediaType = "mediaType";
	private Feed feed;
	private Integer playlistIndex;
	private String mediaUrl = "http://media.Url";

	public Item build() {
		final Item result = new Item();
		result.setPosition(position);
		result.setMediaPath(mediaPath);
		result.setDescription(description);
		result.setUrl(url);
		result.setMediaLength(mediaLength);
		result.setTitle(title);
		result.setPublished(published);
		result.setId(id);
		result.setMediaType(mediaType);
		result.setFeed(feed);
		result.setPlaylistIndex(playlistIndex);
		result.setMediaUrl(mediaUrl);
		return result;
	}

	public static ItemBuilder anItem() {
		return new ItemBuilder();
	}

	public ItemBuilder withPosition(final int position) {
		this.position = position;
		return this;
	}

	public ItemBuilder withMediaPath(final String mediaPath) {
		this.mediaPath = mediaPath;
		return this;
	}

	public ItemBuilder withDescription(final String description) {
		this.description = description;
		return this;
	}

	public ItemBuilder withUrl(final String url) {
		this.url = url;
		return this;
	}

	public ItemBuilder withMediaLength(final Integer mediaLength) {
		this.mediaLength = mediaLength;
		return this;
	}

	public ItemBuilder withTitle(final String title) {
		this.title = title;
		return this;
	}

	public ItemBuilder withPublished(final Date published) {
		this.published = published;
		return this;
	}

	public ItemBuilder withId(final int id) {
		this.id = id;
		return this;
	}

	public ItemBuilder withMediaType(final String mediaType) {
		this.mediaType = mediaType;
		return this;
	}

	public ItemBuilder withFeed(final Feed feed) {
		this.feed = feed;
		return this;
	}

	public ItemBuilder withPlaylistIndex(final Integer playlistIndex) {
		this.playlistIndex = playlistIndex;
		return this;
	}

	public ItemBuilder withMediaUrl(final String mediaUrl) {
		this.mediaUrl = mediaUrl;
		return this;
	}
}
