package com.example.srinivas.flickrapp;

public class GalleryItem {
	private String mCaption;
	private String mId;
	private String mUrl;
	private String mOwner;
	
	public String toString(){
		return mCaption;
	}

	public String getmCaption() {
		return mCaption;
	}

	public void setCaption(String mCaption) {
		this.mCaption = mCaption;
	}

	public String getId() {
		return mId;
	}

	public void setId(String mId) {
		this.mId = mId;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	
	public String getPhotoPageUrl(){
		return "https://www.flickr.com/photos/"+mOwner+"/"+mId;
	}
	
	public String getOwner(){
		return mOwner;
	}
	
	public void setOwner(String owner){
		mOwner=owner;
	}
}
