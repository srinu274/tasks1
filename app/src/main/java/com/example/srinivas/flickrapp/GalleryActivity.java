package com.example.srinivas.flickrapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ThumbnailDownloader<ImageView> mThumbnailThread;
    private int mPageCount=0;
    private ThumbnailInfoDownloader<View> mThumbnailInfoThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView=(android.support.v7.widget.RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(new ListAdapter());
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        startThreads();
        new FetchItemsTask().execute();
    }

    public void startThreads() {
        mThumbnailThread=new ThumbnailDownloader<ImageView>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>(){
            @Override
            public void onThumbnailDownloaded(final ImageView imageView,final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        mThumbnailInfoThread=new ThumbnailInfoDownloader<>(new Handler());
        mThumbnailInfoThread.setListener(new ThumbnailInfoDownloader.Listener<View>(){
            @Override
            public void onThumbnailInfoDownloaded(final View view,final PhotoInfo info) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(info==null) return;
                       setInfoView(view,info);
                    }
                });
            }
        });
        mThumbnailInfoThread.start();
        mThumbnailInfoThread.getLooper();
    }


    private class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        private List<GalleryItem> items;

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(GalleryActivity.this).inflate(R.layout.list_items,parent,false);
            return new ListHolder(v);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            if((position==items.size()-1)){
                new FetchItemsTask().execute();
            }
            holder.setView(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items!=null?items.size():0;
        }

        public void setItems(List<GalleryItem> items) {
            this.items=items;
            notifyDataSetChanged();
        }

        public List<GalleryItem> getItems() {
            return items;
        }

        public void addItems(ArrayList<GalleryItem> result) {
            if(result!=null) {
                if(items==null) items=new ArrayList<>();
                boolean present=false;
                for(GalleryItem current:result) {
                    present=false;
                    for(GalleryItem item:items) {
                        if(item.getId().equals(current.getId())) {
                            present=true;
                            break;
                        }
                    }
                    if(!present) items.add(current);
                }
                notifyDataSetChanged();
            }
        }
    }


    private class ListHolder extends RecyclerView.ViewHolder {

        private ViewFlipper flipper;
        private ImageView imageView;
        private View container;
        private int pos=0;

        public ListHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.image);
            flipper=(ViewFlipper)itemView;
            container=itemView.findViewById(R.id.container_show_info);
            flipper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position==RecyclerView.NO_POSITION) return;
                    GalleryItem item=((ListAdapter)mRecyclerView.getAdapter()).getItems().get(position);
                    if(pos==0) {
                        pos=1;
                        flipper.setInAnimation(GalleryActivity.this,R.anim.card_flip_left_in);
                        flipper.setOutAnimation(GalleryActivity.this,R.anim.card_flip_right_out);
                        mThumbnailInfoThread.queueThumbnailInfo(container,item.getId());
                    } else if(pos==1){
                        pos=0;
                        flipper.setInAnimation(GalleryActivity.this,R.anim.card_flip_right_in);
                        flipper.setOutAnimation(GalleryActivity.this,R.anim.card_flip_left_out);
                        mThumbnailThread.queueThumbnail(imageView, item.getUrl());
                    }
                    flipper.showNext();
                }
            });
        }

        public void setView(GalleryItem item) {
            if(pos==1) {
                pos=0;
                flipper.showNext();
            }
            resetsetInfoView(container);
            imageView.setImageResource(R.mipmap.ic_launcher);
            mThumbnailThread.queueThumbnail(imageView, item.getUrl());
        }

    }


    public static void setInfoView(View view,PhotoInfo info) {
        View containerInfo=view.findViewById(R.id.container_info);
        ProgressBar bar=(ProgressBar)view.findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);
        containerInfo.setVisibility(View.VISIBLE);
        TextView title=(TextView)containerInfo.findViewById(R.id.title);
        TextView description=(TextView)containerInfo.findViewById(R.id.description);
        TextView location=(TextView)containerInfo.findViewById(R.id.location);
        TextView size=(TextView)containerInfo.findViewById(R.id.size);
        String titleString=info.getTitle()!=null?info.getTitle().trim():"";
        String descriptionString=info.getDescription()!=null?info.getDescription().trim():"";
        String locationString=info.getDescription()!=null?info.getLocation().trim():"";
        title.setText(!TextUtils.isEmpty(titleString)?info.getTitle():"No title");
        description.setText(!TextUtils.isEmpty(descriptionString)?info.getDescription():"No Description");
        location.setText(!TextUtils.isEmpty(locationString)?info.getLocation():"No Location");
        size.setText("Thumb nail Size : 75*75");
    }

    public static void resetsetInfoView(View view) {
        View containerInfo=view.findViewById(R.id.container_info);
        TextView title=(TextView)containerInfo.findViewById(R.id.title);
        TextView description=(TextView)containerInfo.findViewById(R.id.description);
        TextView location=(TextView)containerInfo.findViewById(R.id.location);
        TextView size=(TextView)containerInfo.findViewById(R.id.size);
        title.setText("");
        description.setText("");
        location.setText("");
        size.setText("");
        containerInfo.setVisibility(View.INVISIBLE);
        ProgressBar bar=(ProgressBar)view.findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
    }


    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>> {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void...params ){
            return new FlickrFetcher().fetchItems(++mPageCount);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> result){
            ((ListAdapter)mRecyclerView.getAdapter()).addItems(result);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbnailThread.clearQueue();
        mThumbnailThread.quit();
        mThumbnailInfoThread.clearQueue();
        mThumbnailInfoThread.quit();
    }

}
