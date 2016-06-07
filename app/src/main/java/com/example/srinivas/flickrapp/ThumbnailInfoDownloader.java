package com.example.srinivas.flickrapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Srinivas on 07-06-2016.
 */
public class ThumbnailInfoDownloader<Token> extends HandlerThread {

    private static final String TAG = "ThumbnailInfoDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mHandler;
    private Handler mResponseHandler;
    private Listener<Token> mListener;
    private Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    private GlobalPhotoInfoCache mCache;

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for info url: " + requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }

    public interface Listener<Token> {
        void onThumbnailInfoDownloaded(Token token, PhotoInfo info);
    }

    public ThumbnailInfoDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mCache = new GlobalPhotoInfoCache(cacheSize);
    }

    public void setListener(Listener<Token> listener) {
        if (listener != null)
            mListener = listener;
    }

    public void queueThumbnailInfo(Token token, String id) {
        Log.i(TAG, "Got an id :" + id);
        requestMap.put(token, id);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }


    private void handleRequest(final Token token) {

        final String id = requestMap.get(token);
        if (id == null) {
            return;
        }
        PhotoInfo info = mCache.get(id);
        if (info != null) {
            if (requestMap.get(token) != id) return;
            requestMap.remove(token);
            mListener.onThumbnailInfoDownloaded(token, info);
            return;
        }
        final PhotoInfo photoInfo = new FlickrFetcher().fetchPhotoInfo(id);
        mCache.put(id, photoInfo);
        Log.i(TAG, "Info created");
        mResponseHandler.post(new Runnable() {
            public void run() {
                if (requestMap.get(token) != id) return;
                requestMap.remove(token);
                mListener.onThumbnailInfoDownloaded(token, photoInfo);
            }
        });

    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

}
