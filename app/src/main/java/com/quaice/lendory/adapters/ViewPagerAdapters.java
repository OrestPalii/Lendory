package com.quaice.lendory.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ViewPagerAdapters extends PagerAdapter {
    private Context context;
    private ArrayList<String> imageUrls;

    public ViewPagerAdapters(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView zoomableImageView = new PhotoView(context);
        try {
            Picasso.get().load(imageUrls.get(position)).into(zoomableImageView);
            container.addView(zoomableImageView);
        }catch (Exception e) {}
        return zoomableImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
