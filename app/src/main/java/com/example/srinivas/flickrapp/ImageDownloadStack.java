package com.example.srinivas.flickrapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Srinivas on 24-04-2016.
 */
public abstract class ImageDownloadStack {

    /*
    private LinkedList<ImageRequest> data;
    private LruCache<String,Bitmap> cache;
    private Context context;
    private String deviceId;
    private ImageDownloadTask task;

    public ImageDownloadStack(Context context, String deviceId) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize= maxMemory/8;
        cache=new GlobalImageCache(cacheSize);
        this.context=context;
        data=new LinkedList<ImageRequest>();
        this.deviceId=deviceId;
        task=new ImageDownloadTask();
    }


    public ImageDownloadStack(Context context, String deviceId, List<ImageRequest> data) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize= maxMemory/8;
        cache=new GlobalImageCache(cacheSize);
        this.context=context;
        this.data=new LinkedList<ImageRequest>();
        Collections.copy(this.data,data);
        this.deviceId=deviceId;
        task=new ImageDownloadTask();
    }


    public File fetchFile(ImageRequest request) {
        HttpURLConnection connection = null;
        BufferedInputStream input = null;
        FileOutputStream stream=null;
        try {
            Log.i("Network", "send Loc");
            String serverurl = context.getResources().getString(R.string.server_url);
            URL url = new URL(serverurl + "service/getimage");
            connection = (HttpURLConnection) url.openConnection();
            OutputStream output = null;
            connection.setRequestMethod("POST");
            Type token = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = new HashMap<String, String>();
            map.put("image_name",request.getImageName());
            map.put("size","small");
            map.put("user_id",LogInData.getLogInUserId(context));
            String exp = new Gson().toJson(map, token);
            connection.setDoOutput(true);
            output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"));
            writer.println(exp);
            writer.flush();
            Log.i("String", exp);
            writer.close();
            Log.i("response code", connection.getResponseCode() + "");
            //connection.setDoInput(true);
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                input = new BufferedInputStream(connection.getInputStream());
                File f=new File(getFilePath(context),request.getImageName());
                stream=new FileOutputStream(f);
                Log.i("response code", connection.getResponseCode() + "");
                Log.i("String", (input != null) ? input + " is there" : "input null");
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = input.read()) != -1) {
                    stream.write(ch);
                }
                if(f.exists()&& f.length()>0) {
                    return f;
                }
                Log.i("Val", "accepted"+builder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {

                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {

                }
            }
        }
        Log.i("Val", "failed");
        return null;
    }

    public static Bitmap getBitmap(String str){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap= BitmapFactory.decodeFile(str,options);
        return bitmap;
    }

    public static String getFilePath(Context context) {
        File f=new File(context.getExternalFilesDir(null),"temp");
        if(!f.exists()) f.mkdir();
        return f.getPath();
    }

    public void cancelImageRequests(List<ImageRequest> requests) {
        data.removeAll(requests);
    }


    public Bitmap getBitmap(ImageRequest request) {
        Bitmap bitmap=null;
        if(TextUtils.isEmpty(request.getImageId())) return bitmap;
        bitmap=cache.get(request.getImageId());
        return bitmap;
    }

    public void requestImage(ImageRequest request) {
        Bitmap bitmap=null;
        if(TextUtils.isEmpty(request.getImageId())) return;
        bitmap=cache.get(request.getImageId());
        if(bitmap==null) {
            setRunning(true);
            if(data.contains(request)) {
                data.remove(request);
                data.addLast(request);
            }
            data.add(request);
        } else {
            callback(request);
        }
    }

    public void setRunning(boolean flag) {
        if(flag) {
            if(task==null) task=new ImageDownloadTask();
            else if(!task.isAlive()) task=new ImageDownloadTask();
        } else {
            task.setRunning(false);
            task=null;
        }
    }



    public boolean getRunning() {
        return task.getRunning();
    }

    private class ImageDownloadTask extends Thread {

        private boolean isRunning=false;
        private ImageRequest current;

        ImageDownloadTask() {
            isRunning=true;
            start();
        }

        public void setRunning(boolean isRunning) {
            this.isRunning=isRunning;
        }

        public boolean getRunning() {
            return isRunning;
        }

        public void setCurrent(ImageRequest request) {
            this.current=request;
        }

        public ImageRequest getCurrent() {
            return current;
        }

        @Override
        public void run() {
            while(isRunning && data!=null && data.size()>0) {
                current=data.peekLast();
                if(current==null) {
                    continue;
                }
                Bitmap bitmap=null;
                if(TextUtils.isEmpty(current.getImageId())) return;
                bitmap=cache.get(current.getImageId());
                if(bitmap!=null) {
                    callback(current);
                    data.remove(current);
                    current=null;
                    continue;
                }
                File f=fetchFile(current);
                if(f!=null) {
                    Bitmap bmp=getBitmap(f.getPath());
                    cache.put(current.getImageId(),bmp);
                    data.remove(current);
                    callback(current);
                }
                current=null;
            }
        }
    }

    public abstract void callback(ImageRequest request);
    */
}
