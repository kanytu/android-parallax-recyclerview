package com.poliveira.apps.parallaxrecycleradapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

/**
 * Created by bryant1410 on 13/08/2015.
 */
public class ExampleItemViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public ExampleItemViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.textView);
    }
}
