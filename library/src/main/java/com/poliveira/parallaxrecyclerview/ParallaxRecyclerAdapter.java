package com.poliveira.parallaxrecyclerview;

import java.util.List;

public abstract class ParallaxRecyclerAdapter<T> extends AbstractParallaxRecyclerAdapter {
    private float mScrollMultiplier = 0.5f;

    public static class VIEW_TYPES {
        public static final int HEADER = Integer.MAX_VALUE;
        public static final int FIRST_VIEW_SHIFT = Integer.MIN_VALUE;
    }

    private List<T> mData;


    public ParallaxRecyclerAdapter(List<T> data) {
        super();
        mData = data;
    }
//
//    public ParallaxRecyclerAdapter() {
//    }

    public List<T> getData() {
        return mData;
    }
//
    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void insert(T item, int position) {
        mData.add(position, item);
        notifyItemInsertedImpl(position);
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        if (position < 0)
            return;

        mData.remove(item);
        notifyItemRemovedImpl(position);
    }

}
