package de.vanmar.android.ilikepodcasts.bo;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class Item implements Serializable {

	private static final long serialVersionUID = 8617289772343323484L;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String title;

	@DatabaseField
	private String description;

	@DatabaseField
	private String url;

	@DatabaseField
	private Date published;

	@DatabaseField
	private String mediaUrl;

	@DatabaseField
	private Long mediaLength;

	@DatabaseField
	private String mediaPath;

	@DatabaseField
	private String mediaType;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
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

	public Long getMediaLength() {
		return mediaLength;
	}

	public void setMediaLength(final Long mediaLength) {
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

	@Override
	public String toString() {
		return "Item [title=" + title + ", description=" + description
				+ ", url=" + url + "]";
	}
}
