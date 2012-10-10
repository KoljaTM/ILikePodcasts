package de.vanmar.android.ilikepodcasts.bo;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Feed implements Serializable {

	private static final long serialVersionUID = 3305921168913468529L;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String title;

	@DatabaseField
	private String description;

	@DatabaseField
	private String url;

	@DatabaseField
	private Date lastUpdate;

	@ForeignCollectionField(eager = true, orderColumnName = "published", orderAscending = false)
	private ForeignCollection<Item> items;

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

	public ForeignCollection<Item> getItems() {
		return items;
	}

	public void setItems(final ForeignCollection<Item> items) {
		this.items = items;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "Feed [title=" + title + ", description=" + description
				+ ", items=" + items + "]";
	}
}
