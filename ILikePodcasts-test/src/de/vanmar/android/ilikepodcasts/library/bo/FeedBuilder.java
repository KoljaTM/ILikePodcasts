package de.vanmar.android.ilikepodcasts.library.bo;

import java.util.Collection;
import java.util.Date;

public class FeedBuilder {

	private String description = "description";
	private String url = "url";
	private String title = "title";
	private Collection<Item> items;
	private int id;
	private Date lastUpdate;

	public Feed build() {
		final Feed result = new Feed();
		result.setDescription(description);
		result.setUrl(url);
		result.setTitle(title);
		result.setItems(items);
		result.setId(id);
		result.setLastUpdate(lastUpdate);
		return result;
	}

	public static FeedBuilder aFeed() {
		return new FeedBuilder();
	}

	public FeedBuilder withDescription(final String description) {
		this.description = description;
		return this;
	}

	public FeedBuilder withUrl(final String url) {
		this.url = url;
		return this;
	}

	public FeedBuilder withTitle(final String title) {
		this.title = title;
		return this;
	}

	public FeedBuilder withItems(final Collection<Item> items) {
		this.items = items;
		return this;
	}

	public FeedBuilder withId(final int id) {
		this.id = id;
		return this;
	}

	public FeedBuilder withLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}
}
