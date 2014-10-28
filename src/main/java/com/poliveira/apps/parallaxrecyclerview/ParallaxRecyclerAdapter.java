package com.poliveira.apps.parallaxrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by poliveira on 27/10/2014.
 */
public class ParallaxRecyclerAdapter extends RecyclerView.Adapter<ParallaxRecyclerAdapter.ViewHolder> {

    private final List<String> mData;
    private CustomRelativeWrapper mHeader;


    public void addItem(String item, int position) {
        mData.add(position, item);
        notifyItemInserted(position + 1); //we have to add 1 to the notification position since we don't want to mess with the header
    }

    public ParallaxRecyclerAdapter(List<String> myData) {
        mData = myData;
    }

    public void translateHeader(float of) {
        mHeader.setTranslationY(of);
        mHeader.setClipY(Math.round(of));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 1) { //if viewtype==1 then it's the header
            mHeader = new CustomRelativeWrapper(viewGroup.getContext());
            mHeader.addView(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_header, mHeader, false));
            return new ViewHolder(mHeader);
        }
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (i != 0) {//only bind the rows, not the header(i==0)
            viewHolder.textView.setText(mData.get(i - 1)); //notice the (i-1). You must do this because onBindViewHolder will have the header (i==0) into account.
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 2;//position==0 -> header | position!=0 -> row
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size() + 1; //remember to add 1 to the getItemCount because of the header.
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    static class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;

        public CustomRelativeWrapper(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset)); //this will clip the header so our rows don't overlap the header
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }
}
