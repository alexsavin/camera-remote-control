package com.fuckolympus.arc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.fuckolympus.arc.camera.vo.ImageFile;
import com.fuckolympus.arc.util.Callback;
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
        session.getCameraApi().switchToPlayMode(this, new Callback<String>() {
            @Override
            public void apply(String arg) {
                getImages();
            }
        }, failureCallback);
    }

    private void getImages() {
        session.getCameraApi().getImageList(this, new Callback<List<ImageFile>>() {
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
            return imageFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            ImageFile imageFile = imageFiles.get(position);
            String fileName = String.format("%s/%s", imageFile.path, imageFile.name);
            session.getCameraApi().getThumbnail(context, fileName, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT,
                    new Callback<Bitmap>() {
                        @Override
                        public void apply(Bitmap arg) {
                            imageView.setImageBitmap(arg);
                        }
                    },
                    new Callback<String>() {
                        @Override
                        public void apply(String arg) {
                            imageView.setImageResource(R.drawable.images_256);
                        }
                    }
            );

            return imageView;
        }
    }
}
