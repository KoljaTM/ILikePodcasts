package de.vanmar.android.ilikepodcasts.library.search;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class SearchResultItem implements Parcelable {
	private String artistName;
	private String collectionName;
	private String feedUrl;
	private Drawable image;
	private String artworkUrl60;

	public String getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(final String feedUrl) {
		this.feedUrl = feedUrl;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(final String collectionName) {
		this.collectionName = collectionName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(final Drawable image) {
		this.image = image;
	}

	public String getArtworkUrl60() {
		return artworkUrl60;
	}

	public void setArtworkUrl60(final String artworkUrl60) {
		this.artworkUrl60 = artworkUrl60;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		// TODO Auto-generated method stub

	}

}
