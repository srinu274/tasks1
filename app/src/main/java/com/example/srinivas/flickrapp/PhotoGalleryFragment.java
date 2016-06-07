package com.example.srinivas.flickrapp;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;


public class PhotoGalleryFragment extends Fragment {

	/*
	private GridView mGridView;
	ArrayList<GalleryItem> mItems;
	ThumbnailDownloader<ImageView> mThumbnailThread;
	private static final String TAG="PhotoGalleryFragment";
	private int mPageCount=0;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		updateItems();
		Log.i(TAG,"after service call");
		mThumbnailThread=new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>(){
			@Override
			public void onThumbnailDownloaded(ImageView imageView,Bitmap bitmap){
				if(isVisible())
					imageView.setImageBitmap(bitmap);
			}
		});
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
		Log.i(TAG, "Background thread started");
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
		View v=(View)inflater.inflate(R.layout.fragment_photo_gallery,null);
		mGridView=(GridView)v.findViewById(R.id.gridView);
		mGridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> gridView,View view,int pos,long id){
				GalleryItem item=mItems.get(pos);
				Uri photoUri=Uri.parse(item.getPhotoPageUrl());
				Intent i=new Intent(getActivity(),PhotoPageActivity.class);
				i.setData(photoUri);
				startActivity(i);
			}
		});
		setupAdapter();
		return v;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mThumbnailThread.quit();
		Log.i(TAG, "Background thread destroyed");
	}
	
	
	private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>>{
		@Override
		protected ArrayList<GalleryItem> doInBackground(Void...params ){
			Activity activity=getActivity();
			if(activity==null)
				return new ArrayList<GalleryItem>();
			String query=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(FlickrFetcher.PREF_SEARCH_QUERY, null);
			if(query==null){
			return new FlickrFetcher().fetchItems(++mPageCount);
			}else{
				return new FlickrFetcher().search(query);
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> result){
			mItems=result;
			setupAdapter();
		}
	}
	
	void setupAdapter() {
		if (getActivity() == null || mGridView == null) return;
		if (mItems != null) {
		mGridView.setAdapter(new GalleryItemAdapter(mItems));
		} else {
		mGridView.setAdapter(null);
		}
	}
	
	
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem>{
		GalleryItemAdapter(ArrayList<GalleryItem> items){
			super(getActivity(), 0, items);
		}
		
		@Override
		public View getView(int pos,View convertView,ViewGroup parent){
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater()
				.inflate(R.layout.gallery_item, parent, false);
				}
			convertView= getActivity().getLayoutInflater().inflate(R.layout.gallery_item, null);
			ImageView iv=(ImageView)convertView.findViewById(R.id.gallery_item_ImageView);
			iv.setImageResource(R.drawable.baby);
			GalleryItem item=getItem(pos);
			mThumbnailThread.queueThumbnail(iv, item.getUrl());
			return convertView;
		}
	}
	
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		mThumbnailThread.clearQueue();
	}
	
	@Override
	@TargetApi(11)
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		Log.i(TAG,"Created options menu");
		inflater.inflate(R.menu.options_menu, menu);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			Log.i(TAG,"in  Created options menu");
			MenuItem searchItem=menu.findItem(R.id.menu_item_search);
			SearchView searchView=(SearchView)searchItem.getActionView();
			SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
			ComponentName name=getActivity().getComponentName();
			SearchableInfo info=searchManager.getSearchableInfo(name);
			searchView.setSearchableInfo(info);
			Log.i(TAG,"Logged :"+info);
		}
	}
	
	
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_item_search:
			getActivity().onSearchRequested();
			return true;
		case R.id.menu_item_clear:
			PreferenceManager.getDefaultSharedPreferences(getActivity())
				.edit()
				.putString(FlickrFetcher.PREF_SEARCH_QUERY,null)
				.commit();
				updateItems();
			return true;
		case R.id.menu_item_toggle_polling:
			boolean poll=!PollService.isServiceAlarmOn(getActivity());
			PollService.setServiceAlarm(getActivity(), poll);
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
				getActivity().invalidateOptionsMenu();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		
		MenuItem item=menu.findItem(R.id.menu_item_toggle_polling);
		if(PollService.isServiceAlarmOn(getActivity())){
			item.setTitle(R.string.stop_polling);
		}else{
			item.setTitle(R.string.start_polling);
		}
	}


	
	public void updateItems(){
		new FetchItemsTask().execute();
	}
	*/
}
