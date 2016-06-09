package com.example.srinivas.flickrapp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.util.Log;

import com.example.srinivas.flickrapp.data.GalleryItem;
import com.example.srinivas.flickrapp.data.PhotoInfo;

public class FlickrFetcher {
	public static final String TAG = "FlickrFetchr";
	public static final String PREF_SEARCH_QUERY = "searchQuery";
	public static final String PREF_LAST_RESULT_ID="lastResultId";
	
	private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
	private static final String API_KEY = "e545929c1f43c907fd0fd6bdce9f0f04";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_GET_INFO="flickr.photos.getInfo";
    private static final String PARAM_PHOTO_ID="photo_id";
	private static final String PARAM_EXTRAS = "extras";
	private static final String EXTRA_SMALL_URL = "url_s";
	private static final String XML_PHOTO = "photo";
	private static final String XML_PAGE="page";
	private static final String METHOD_SEARCH="flickr.photos.search";
	private static final String PARAM_TEXT="text";
    private static final String XML_OWNER = "owner";
    private static final String XML_DESCRIPION = "description";
    private static final String XML_TITLE = "title";
    private static final String XML_NOTES="notes";
    private static final String XML_NOTE="note";

    public byte[] getUrlBytes(String urlspec)throws IOException{
		URL url=new URL(urlspec);
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
		try{
		InputStream in=connection.getInputStream();
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		
		if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
			return null;
		}
		int bytesRead=0;
		byte b[]=new byte[1024];
		while((bytesRead=in.read(b))>0){
			out.write(b,0,bytesRead);
		}
		return out.toByteArray();
		}
		finally{
			connection.disconnect();
		}
	}
	
	public String getUrl(String urlSpec) throws IOException{
		return new String(getUrlBytes(urlSpec));
	}
	
	public ArrayList<GalleryItem> fetchItems(int pageno){
		ArrayList<GalleryItem> items=new ArrayList<GalleryItem>();
		String url = Uri.parse(ENDPOINT).buildUpon()
				.appendQueryParameter("method", METHOD_GET_RECENT)
				.appendQueryParameter("api_key", API_KEY)
				.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
				.appendQueryParameter(XML_PAGE, ""+pageno)
				.toString();
		Log.i(TAG, url);
		return downloadGalleryItems(url);
	}
	
	
	public void parseItems(ArrayList<GalleryItem> items,XmlPullParser parser) throws XmlPullParserException, IOException{
	
		int event=parser.next();
		while(event!=XmlPullParser.END_DOCUMENT){
			if(event==XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())){
				String id=parser.getAttributeValue(null, "id");
				String title=parser.getAttributeValue(null, "title");
				String url_s=parser.getAttributeValue(null, EXTRA_SMALL_URL);
				String owner=parser.getAttributeValue(null,"owner");
				
				GalleryItem item=new GalleryItem();
				item.setCaption(title);
				item.setId(id);
				item.setUrl(url_s);
				item.setOwner(owner);
				items.add(item);
			}
			event=parser.next();
		}
	}

    public PhotoInfo fetchPhotoInfo(String photoId){
        try {
            ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
            String url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GET_INFO)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter(PARAM_PHOTO_ID, photoId)
                    .toString();
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            return parsePhotoInfo(parser);
        } catch(IOException ie){
            Log.i(TAG,"Failed to load xml");
        } catch (XmlPullParserException e) {
            Log.i(TAG,"Failed to pull xml");
        }
        return null;
    }

    public PhotoInfo parsePhotoInfo(XmlPullParser parser) throws XmlPullParserException,IOException {
        int event=parser.next();
        PhotoInfo info=new PhotoInfo();
        while(event!=XmlPullParser.END_DOCUMENT){
            if(event==XmlPullParser.START_TAG && XML_OWNER.equals(parser.getName())){
                parser.next();
                String location=parser.getAttributeValue(null, "location");
                info.setLocation(location);
            } else if(event==XmlPullParser.START_TAG && XML_TITLE.equals(parser.getName())) {
                parser.next();
                String title=parser.getText();
                Log.i("in info title",title+"");
                info.setTitle(title);
            } else if(event==XmlPullParser.START_TAG && XML_DESCRIPION.equals(parser.getName())) {
                parser.next();
                String description=parser.getText();
                info.setDescription(description);
            }
            event=parser.next();
        }
        return info;
    }

	public ArrayList<GalleryItem> downloadGalleryItems(String url){
		ArrayList<GalleryItem> items=new ArrayList<GalleryItem>();
		try{
			String xmlString = getUrl(url);
			Log.i(TAG, "Received xml: " + xmlString);
			XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
			XmlPullParser parser=factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			parseItems(items,parser);
		}catch(IOException ie){
			Log.i(TAG,"Failed to load xml"+items.size());
		} catch (XmlPullParserException e) {
			Log.i(TAG,"Failed to pull xml"+items.size());
		}
		return items;
	}
	
	
	public ArrayList<GalleryItem> search(String query){
		String url = Uri.parse(ENDPOINT).buildUpon()
				.appendQueryParameter("method", METHOD_SEARCH)
				.appendQueryParameter("api_key", API_KEY)
				.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
				.appendQueryParameter(PARAM_TEXT,query)
				.toString();
		Log.i(TAG, url);
		return downloadGalleryItems(url);
	}
}
