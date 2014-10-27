package com.poliveira.apps.parallaxrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by poliveira on 27/10/2014.
 */
public class ParallaxRecyclerAdapter extends RecyclerView.Adapter<ParallaxRecyclerAdapter.ViewHolder> {

    private final List<String> mData;
    private CustomRelativeWrapper mHeader;

    public ParallaxRecyclerAdapter(List<String> myData) {
        mData = myData;
    }

    public void translateHeader(float offset) {
        mHeader.setTranslationY(offset);
        mHeader.setClipY(Math.round(offset));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 1) {
            mHeader = new CustomRelativeWrapper(viewGroup.getContext());
            mHeader.addView(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_header, mHeader, false));
            return new ViewHolder(mHeader);
        }
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 2;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size() + 1;
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
