package com.poliveira.apps.parallaxrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by poliveira on 27/10/2014.
 */
public class ParallaxRecyclerView extends RecyclerView {
    private int mCurrentOffset;

    public ParallaxRecyclerView(Context context) {
        super(context);
        init();
    }

    public ParallaxRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParallaxRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentOffset += dy;
                ((ParallaxRecyclerAdapter) getAdapter()).translateHeader(mCurrentOffset * 0.5f);
            }
        });
    }

    public int getCurrentOffset() {
        return mCurrentOffset;
    }

    public void setCurrentOffset(int currentOffset) {
        mCurrentOffset = currentOffset;
    }

}
