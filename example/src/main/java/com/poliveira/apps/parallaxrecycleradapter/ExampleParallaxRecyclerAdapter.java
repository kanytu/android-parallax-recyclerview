package com.poliveira.apps.parallaxrecycleradapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import java.util.List;

/**
 * Created by bryant1410 on 13/08/2015.
 */
public class ExampleParallaxRecyclerAdapter extends ParallaxRecyclerAdapter<ExampleItemViewHolder> {
    private List<String> mContent;
    private Context mContext;
    private boolean mUseCards;

    public ExampleParallaxRecyclerAdapter(List<String> content, View header, RecyclerView view, boolean shouldClipView, Context context, boolean useCards) {
        super(header, view, shouldClipView);
        mContent = content;
        mContext = context;
        mUseCards = useCards;
    }

    @Override
    public ExampleItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
        final ExampleItemViewHolder holder;
        if (mUseCards) {
            holder = new ExampleItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_recyclerview_cards, parent, false));
        } else {
            holder = new ExampleItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_recyclerview, parent, false));
        }

        //don't set listeners on onBindViewHolder. For more info check http://androidshenanigans.blogspot.pt/2015/02/viewholder-pattern-common-mistakes.html
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, holder.textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindItemViewHolder(ExampleItemViewHolder holder, int position) {
        holder.textView.setText(mContent.get(position));
    }

    @Override
    public int getItemCountWithoutHeader() {
        return mContent.size();
    }
}
