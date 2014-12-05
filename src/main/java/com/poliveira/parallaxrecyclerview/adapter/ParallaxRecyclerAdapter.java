package com.poliveira.parallaxrecyclerview.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;


/**
 * Created by poliveira on 03/11/2014.
 */
public class ParallaxRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final float SCROLL_MULTIPLIER = 0.5f;

    private class VIEW_TYPES {
        public static final int NORMAL = 1;
        public static final int HEADER = 2;
        public static final int FIRST_VIEW = 3;
    }

    public interface RecyclerAdapterMethods {
        void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i);

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i);

        int getItemCount();
    }


    public interface OnClickEvent {
        /**
         * Event triggered when you click on a item of the adapter
         *
         * @param v        view
         * @param position position on the array
         */
        void onClick(View v, int position);
    }

    public interface OnParallaxScroll {
        /**
         * Event triggered when the parallax is being scrolled.
         *
         * @param percentage
         * @param offset
         * @param parallax
         */
        void onParallaxScroll(float percentage, float offset, View parallax);
    }

    private List<T> mData;
    private CustomRelativeWrapper mHeader;
    private RecyclerAdapterMethods mRecyclerAdapterMethods;
    private OnClickEvent mOnClickEvent;
    private OnParallaxScroll mParallaxScroll;
    private RecyclerView mRecyclerView;

    public void translateHeader(float of) {
        float ofCalculated = of * SCROLL_MULTIPLIER;
        mHeader.setTranslationY(ofCalculated);
        mHeader.setClipY(Math.round(ofCalculated));
        if (mParallaxScroll != null) {
            float left = Math.min(1, ((ofCalculated) / (mHeader.getHeight() * SCROLL_MULTIPLIER)));
            mParallaxScroll.onParallaxScroll(left, of, mHeader);
        }
    }

    public void setParallaxHeader(View header, final RecyclerView view) {
        mRecyclerView = view;
        mHeader = new CustomRelativeWrapper(header.getContext());
        mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mHeader != null) {
                    RecyclerView.ViewHolder holder = view.findViewHolderForPosition(0);
                    if (holder != null)
                        translateHeader(-holder.itemView.getTop());
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");
        if (i != 0 && mHeader != null)
            mRecyclerAdapterMethods.onBindViewHolder(viewHolder, i - 1);
        else if (i != 0)
            mRecyclerAdapterMethods.onBindViewHolder(viewHolder, i);
        if (mOnClickEvent != null)
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickEvent.onClick(v, i - (mHeader == null ? 0 : 1));
                }
            });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");
        if (i == VIEW_TYPES.HEADER && mHeader != null)
            return new ViewHolder(mHeader);
        if (i == VIEW_TYPES.FIRST_VIEW && mHeader != null && mRecyclerView != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForPosition(0);
            if (holder != null)
                translateHeader(-holder.itemView.getTop());
        }
        return mRecyclerAdapterMethods.onCreateViewHolder(viewGroup, i);
    }

    public void setOnClickEvent(OnClickEvent onClickEvent) {
        mOnClickEvent = onClickEvent;
    }

    public void setOnParallaxScroll(OnParallaxScroll parallaxScroll) {
        mParallaxScroll = parallaxScroll;
        mParallaxScroll.onParallaxScroll(0, 0, mHeader);
    }

    public ParallaxRecyclerAdapter(List<T> data) {
        mData = data;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addItem(T item, int position) {
        mData.add(position, item);
        notifyItemInserted(position + (mHeader == null ? 0 : 1));
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        if (position < 0)
            return;
        mData.remove(item);
        notifyItemRemoved(position + (mHeader == null ? 0 : 1));
    }


    public int getItemCount() {


        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");
        return mRecyclerAdapterMethods.getItemCount() + (mHeader == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");
        if (position == 1)
            return VIEW_TYPES.FIRST_VIEW;
        return position == 0 ? VIEW_TYPES.HEADER : VIEW_TYPES.NORMAL;
    }

    public void implementRecyclerAdapterMethods(RecyclerAdapterMethods callbacks) {
        mRecyclerAdapterMethods = callbacks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;

        public CustomRelativeWrapper(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }
}
