package de.vanmar.android.ilikepodcasts.library.bo;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class Item implements Serializable {

	private static final long serialVersionUID = 8617289772343323484L;

	public static final String ID = "_id";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String URL = "url";
	public static final String PUBLISHED = "published";
	public static final String MEDIA_URL = "media_url";
	public static final String MEDIA_LENGTH = "media_length";
	public static final String MEDIA_PATH = "media_path";
	public static final String MEDIA_TYPE = "media_type";
	public static final String FEED = "feed_id";
	public static final String PLAYLIST_INDEX = "playlist_index";
	public static final String POSITION = "position";

	@DatabaseField(generatedId = true, columnName = ID)
	private int id;

	@DatabaseField(columnName = TITLE)
	private String title;

	@DatabaseField(columnName = DESCRIPTION)
	private String description;

	@DatabaseField(columnName = URL, unique = true)
	private String url;

	@DatabaseField(columnName = PUBLISHED)
	private Date published;

	@DatabaseField(columnName = MEDIA_URL)
	private String mediaUrl;

	@DatabaseField(columnName = MEDIA_LENGTH)
	private Integer mediaLength;

	@DatabaseField(columnName = MEDIA_PATH)
	private String mediaPath;

	@DatabaseField(columnName = MEDIA_TYPE)
	private String mediaType;

	@DatabaseField(columnName = PLAYLIST_INDEX)
	private Integer playlistIndex;

	@DatabaseField(columnName = POSITION)
	private int position;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FEED)
	private Feed feed;

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Date getPublished() {
		return published;
	}

	public void setPublished(final Date published) {
		this.published = published;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(final Feed feed) {
		this.feed = feed;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(final String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public Integer getMediaLength() {
		return mediaLength;
	}

	public void setMediaLength(final Integer mediaLength) {
		this.mediaLength = mediaLength;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(final String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(final String mediaType) {
		this.mediaType = mediaType;
	}

	public Integer getPlaylistIndex() {
		return playlistIndex;
	}

	public void setPlaylistIndex(final Integer playlistIndex) {
		this.playlistIndex = playlistIndex;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(final int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Item [title=" + title + ", description=" + description
				+ ", url=" + url + "]";
	}
}
