package com.poliveira.parallaxrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public abstract class AbstractParallaxRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private float mScrollMultiplier = 0.5f;

    public static class VIEW_TYPES {
        public static final int HEADER = Integer.MAX_VALUE;
        public static final int FIRST_VIEW_SHIFT = Integer.MIN_VALUE;
    }

    public abstract void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, int pos);

    public abstract RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, int viewType);

    public abstract int getItemCountImpl();

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
         */
        void onParallaxScroll(float percentage, float offset, View parallax);
    }

    private CustomRelativeWrapper mHeader;
    private OnClickEvent mOnClickEvent;
    private OnParallaxScroll mParallaxScroll;
    private RecyclerView mRecyclerView;
    private boolean mShouldClipView = true;

    /**
     * Translates the adapter in Y
     *
     * @param of offset in px
     */
    public void translateHeader(float of) {
        float ofCalculated = of * mScrollMultiplier;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && of < mHeader.getHeight()) {
            mHeader.setTranslationY(ofCalculated);
        } else if (of < mHeader.getHeight()) {
            TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            mHeader.startAnimation(anim);
        }
        mHeader.setClipY(Math.round(ofCalculated));
        if (mParallaxScroll != null) {
            int firstVisible = getFirstVisibleIndex();
            final RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(0);

            float left;
            if (holder != null && firstVisible == 0) {
                left = Math.min(1, ((ofCalculated) / (mHeader.getHeight() * mScrollMultiplier)));
            }else {
                left = 1;
            }
            mParallaxScroll.onParallaxScroll(left, of, mHeader);
        }
    }

    private int getFirstVisibleIndex() {
        if (mRecyclerView == null)
            return -1;

        LinearLayoutManager layoutManager = (LinearLayoutManager)mRecyclerView.getLayoutManager();

        if (layoutManager == null)
            return -1;

        return layoutManager.findFirstVisibleItemPosition();
    }

    /**
     * Set the view as header.
     *
     * @param header The inflated header
     * @param view   The RecyclerView to set scroll listeners
     */
    public void setParallaxHeader(View header, final RecyclerView view) {
        mRecyclerView = view;

        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
        //OK
        }else{
            throw new IllegalArgumentException("Should use only LinearLayoutManager");
        }
        mHeader = new CustomRelativeWrapper(header.getContext(), mShouldClipView);
        mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mHeader != null) {
                    translateHeader(mRecyclerView.getLayoutManager().getChildAt(0) == mHeader ?
                            mRecyclerView.computeVerticalScrollOffset() : mHeader.getHeight());
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (hasHeader()) {
            if (position == 0) {
                return;
            }
            onBindViewHolderImpl(viewHolder, position - 1);
        } else {
            onBindViewHolderImpl(viewHolder, position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int type) {
        if (type == VIEW_TYPES.HEADER && hasHeader()) {
            return new ViewHolder(mHeader);
        }

        if (isFirstViewType(type)  && hasHeader() && mRecyclerView != null) {
//        if (type == VIEW_TYPES.FIRST_VIEW && mHeader != null && mRecyclerView != null) {
            final RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                translateHeader(-holder.itemView.getTop());
            }
        }

        int actualType = isFirstViewType(type) ? convertFromInternalItemType(type) : type;
        final RecyclerView.ViewHolder holder = onCreateViewHolderImpl(viewGroup, actualType);
        if (mOnClickEvent != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickEvent.onClick(v, holder.getAdapterPosition() - (!hasHeader() ? 0 : 1));
                }
            });
        }
        return holder;
    }

    /**
     * @return true if there is a header on this adapter, false otherwise
     */
    public boolean hasHeader() {
        return mHeader != null;
    }

    public void setOnClickEvent(OnClickEvent onClickEvent) {
        mOnClickEvent = onClickEvent;
    }


    public boolean isShouldClipView() {
        return mShouldClipView;
    }

    /**
     * Defines if we will clip the layout or not. MUST BE CALLED BEFORE {@link
     * #setParallaxHeader(View, RecyclerView)}
     */
    public void setShouldClipView(boolean shouldClickView) {
        mShouldClipView = shouldClickView;
    }

    public void setOnParallaxScroll(OnParallaxScroll parallaxScroll) {
        mParallaxScroll = parallaxScroll;
        mParallaxScroll.onParallaxScroll(0, 0, mHeader);
    }
    public AbstractParallaxRecyclerAdapter() {
    }


    public void notifyItemInsertedImpl(int position) {
        super.notifyItemInserted(position + (!hasHeader() ? 0 : 1));
    }

    public int translatePosition(int pos){
        return pos + (!hasHeader() ? 0 : 1);
    };

    public void notifyItemRemovedImpl(int position) {
        if (position < 0)
            return;
        notifyItemRemoved(position + (!hasHeader() ? 0 : 1));
    }

    public int getItemCount() {
        return getItemCountImpl() + (!hasHeader() ? 0 : 1);
    }

    @Override
    public int getItemViewType(int pos) {
        if (!hasHeader()){
            return getItemViewTypeImpl(pos);
        }

        // here wi do have header
        if (pos == 0){
            return VIEW_TYPES.HEADER;
        }

        int realType = getItemViewTypeImpl(pos-1);
        if (pos == 1) {
            return convertToInternalItemType(realType);
        }

        return realType;
    }

    private boolean isHeaderType(int type){
        return type == VIEW_TYPES.HEADER;
    }
//
    private boolean isFirstViewType(int type){
        return type < 0;
    }

    private int convertToInternalItemType(int type){
        return VIEW_TYPES.FIRST_VIEW_SHIFT + type;
    }

    private int convertFromInternalItemType(int type){
        return VIEW_TYPES.FIRST_VIEW_SHIFT + type;
    }

    abstract public int getItemViewTypeImpl(int position);

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;
        private boolean mShouldClip;

        public CustomRelativeWrapper(Context context, boolean shouldClick) {
            super(context);
            mShouldClip = shouldClick;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (mShouldClip) {
                canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
            }
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }
    /**
     * Set parallax scroll multiplier.
     *
     * @param mul The multiplier
     */
    public void setScrollMultiplier(float mul) {
        this.mScrollMultiplier = mul;
    }

    /**
     * Get the current parallax scroll multiplier.
     *
     */
    public float getScrollMultiplier() {
        return this.mScrollMultiplier;
    }
}
