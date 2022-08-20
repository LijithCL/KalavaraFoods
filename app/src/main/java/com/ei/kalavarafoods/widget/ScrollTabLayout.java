package com.ei.kalavarafoods.widget;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;

/**
 * Created by vishnu.benny on 12/24/2017.
 */

public class ScrollTabLayout extends TabLayout {
    public ScrollTabLayout(Context context) {
        super(context);
    }

    public ScrollTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        try {
            if (getTabCount() == 0)
                return;
            Field field = TabLayout.class.getDeclaredField("mScrollableTabMinWidth");
            field.setAccessible(true);
            field.set(this, (int) (getMeasuredWidth() / (float) getTabCount()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
