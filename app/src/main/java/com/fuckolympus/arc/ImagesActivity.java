package com.fuckolympus.arc;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.fuckolympus.arc.camera.vo.ImageFile;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.DefaultFailureCallback;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class ImagesActivity extends SessionAwareActivity {

    private DefaultFailureCallback failureCallback = new DefaultFailureCallback(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        GridView imagesGrid = (GridView) findViewById(R.id.imagesGrid);
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

                // todo - rewrite it using Volley library
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    adapter.notifyDataSetChanged();
                }
            }
        }, failureCallback);
    }

    private class ImageAdapter extends BaseAdapter {

        private Context context;

        private List<ImageFile> imageFiles = Collections.emptyList();

        ImageAdapter(Context context) {
            this.context = context;
        }

        public void setImageFiles(List<ImageFile> imageFiles) {
            this.imageFiles = imageFiles;
        }

        @Override
        public int getCount() {
            return imageFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return imageFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(160, 120));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            try {
                URL url = new URL(imageFiles.get(position).thumbnailPath);
                imageView.setImageBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
            } catch (IOException e) {
                imageView.setImageResource(R.drawable.images_256);
            }
            return imageView;
        }
    }
}
