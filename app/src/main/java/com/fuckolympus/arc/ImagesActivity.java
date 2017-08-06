package com.fuckolympus.arc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.android.volley.toolbox.NetworkImageView;
import com.fuckolympus.arc.camera.api.CameraApi;
import com.fuckolympus.arc.camera.vo.ImageFile;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.ImageLoaderRequestQueue;
import com.fuckolympus.arc.util.DefaultFailureCallback;

import java.util.Collections;
import java.util.List;

public class ImagesActivity extends SessionAwareActivity {

    public static final int THUMBNAIL_WIDTH = 160;
    public static final int THUMBNAIL_HEIGHT = 120;

    private DefaultFailureCallback failureCallback = new DefaultFailureCallback(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        final GridView imagesGrid = (GridView) findViewById(R.id.imagesGrid);
        imagesGrid.setAdapter(new ImageAdapter(this));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ImagesActivity.this, MenuActivity.class);
        ImagesActivity.this.startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        switchToPlayMode();
    }

    private void switchToPlayMode() {
        session.getCameraApi().switchToPlayMode(new Callback<String>() {
            @Override
            public void apply(String arg) {
                getImages();
            }
        }, failureCallback);
    }

    private void getImages() {
        session.getCameraApi().getImageList(new Callback<List<ImageFile>>() {
            @Override
            public void apply(List<ImageFile> arg) {
                GridView imagesGrid = (GridView) findViewById(R.id.imagesGrid);
                ImageAdapter adapter = (ImageAdapter) imagesGrid.getAdapter();
                adapter.setImageFiles(arg);
            }
        }, failureCallback);
    }

    private class ImageAdapter extends BaseAdapter {

        private Context context;

        private List<ImageFile> imageFiles = Collections.emptyList();

        ImageAdapter(Context context) {
            this.context = context;
        }

        void setImageFiles(List<ImageFile> imageFiles) {
            this.imageFiles = imageFiles;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imageFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final NetworkImageView imageView;
            Log.d(this.getClass().getName(), "position " + position);
            if (convertView == null) {
                imageView = new NetworkImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(THUMBNAIL_HEIGHT, THUMBNAIL_HEIGHT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageResource(R.drawable.images_256);
            } else {
                imageView = (NetworkImageView) convertView;
            }

            final ImageFile imageFile = imageFiles.get(position);
            String fileName = String.format("%s/%s", imageFile.path, imageFile.name);
            imageView.setImageUrl(String.format(CameraApi.CAMERA_URL + CameraApi.GET_THUMBNAIL, fileName),
                    ImageLoaderRequestQueue.getInstance(ImagesActivity.this.getApplicationContext()).getImageLoader());

            return imageView;
        }
    }
}
