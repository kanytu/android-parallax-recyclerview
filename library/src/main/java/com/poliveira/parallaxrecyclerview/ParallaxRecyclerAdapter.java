package com.poliveira.parallaxrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by poliveira on 03/11/2014.
 */
public abstract class ParallaxRecyclerAdapter<HVH extends ParallaxRecyclerAdapter.HeaderViewHolder, IVH extends ParallaxRecyclerAdapter.ItemViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final float SCROLL_MULTIPLIER = 0.5f;

    public static class VIEW_TYPES {
        public static final int NORMAL = 1;
        public static final int HEADER = 2;
        public static final int FIRST_VIEW = 3;
    }

    private CustomRelativeWrapper mHeader;
    private RecyclerView mRecyclerView;
    private int mTotalYScrolled;
    private boolean mShouldClipView = true;

    @SuppressWarnings("UnusedParameters")
    public void onParallaxScroll(float percentage, float offset, View parallax) {

    }

    public abstract HVH onCreateHeaderViewHolder(ViewGroup parent);

    public abstract IVH onCreateItemViewHolder(ViewGroup parent);

    @SuppressWarnings("UnusedParameters")
    public void onBindHeaderViewHolder(HVH viewHolder, final int position) {

    }

    public abstract void onBindItemViewHolder(IVH viewHolder, final int position);

    public abstract int getItemCountWithoutHeader();

    public ParallaxRecyclerAdapter(View header, final RecyclerView view, boolean shouldClipView) {
        mShouldClipView = shouldClipView;
        mRecyclerView = view;
        mHeader = new CustomRelativeWrapper(header.getContext(), mShouldClipView);
        mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalYScrolled += dy;
                translateHeader(mTotalYScrolled);
            }
        });
    }

    /**
     * Translates the adapter in Y
     *
     * @param offset offset in px
     */
    public void translateHeader(float offset) {
        float offsetCalculated = offset * SCROLL_MULTIPLIER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mHeader.setTranslationY(offsetCalculated);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, offsetCalculated, offsetCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            mHeader.startAnimation(anim);
        }
        mHeader.setClipY(Math.round(offsetCalculated));

        float left = Math.min(1, ((offsetCalculated) / (mHeader.getHeight() * SCROLL_MULTIPLIER)));
        onParallaxScroll(left, offset, mHeader);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (position == 0) {
            //noinspection unchecked
            onBindHeaderViewHolder((HVH) viewHolder, position - 1);
        } else {
            //noinspection unchecked
            onBindItemViewHolder((IVH) viewHolder, position - 1);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPES.HEADER) {
            return onCreateHeaderViewHolder(parent);
        }
        if (viewType == VIEW_TYPES.FIRST_VIEW && mRecyclerView != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                translateHeader(-holder.itemView.getTop());
                mTotalYScrolled = -holder.itemView.getTop();
            }
        }
        return onCreateItemViewHolder(parent);
    }

    @SuppressWarnings("unused")
    public boolean isShouldClipView() {
        return mShouldClipView;
    }

    public int getItemCount() {
        return getItemCountWithoutHeader() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return VIEW_TYPES.HEADER;
            case 1:
                return VIEW_TYPES.FIRST_VIEW;
            default:
                return VIEW_TYPES.NORMAL;
        }
    }

    static class CustomRelativeWrapper extends RelativeLayout {
        private int mOffset;
        private boolean mShouldClip;

        public CustomRelativeWrapper(Context context) {
            this(context, true);
        }

        public CustomRelativeWrapper(Context context, boolean shouldClip) {
            super(context);
            mShouldClip = shouldClip;
        }

        @Override
        protected void dispatchDraw(@NonNull Canvas canvas) {
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

    public static abstract class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static abstract class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
