package com.ei.kalavarafoods.ui.main;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.api.Slide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInfalter;
    List<Slide> mSlides;

    public SliderAdapter(Context context, List<Slide> slideList){
        mContext = context;
        mSlides = slideList;
    }
    @Override
    public int getCount() {
        return mSlides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mLayoutInfalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInfalter.inflate(R.layout.pager_slider_layout, container, false);
        ImageView imageBanner = view.findViewById(R.id.imageBanner);
        Picasso.with(mContext)
                .load(mSlides.get(position).getSlid())
                .into(imageBanner);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
