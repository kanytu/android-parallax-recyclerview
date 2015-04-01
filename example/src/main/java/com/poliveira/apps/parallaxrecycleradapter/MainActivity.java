package com.poliveira.apps.parallaxrecycleradapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.poliveira.parallaxrecycleradapter.HeaderLayoutManagerFixed;
import com.poliveira.parallaxrecycleradapter.ParallaxRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poliveira on 26/02/2015.
 */
public class MainActivity extends Activity {

    private boolean isNormalAdapter = false;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        createAdapter(mRecyclerView, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.simple_adapter:
                createAdapter(mRecyclerView, false);
                break;
            case R.id.different_type_adapter:
                createAdapter(mRecyclerView, true);
                break;
            case R.id.card_view_adapter:
                createCardAdapter(mRecyclerView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void createCardAdapter(RecyclerView recyclerView) {
        final List<String> content = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            content.add("item " + i);
        }
        final ParallaxRecyclerAdapter<String> adapter = new ParallaxRecyclerAdapter<>(content);
        HeaderLayoutManagerFixed layoutManagerFixed = new HeaderLayoutManagerFixed(this);
        recyclerView.setLayoutManager(layoutManagerFixed);
        View header = getLayoutInflater().inflate(R.layout.header, recyclerView, false);
        layoutManagerFixed.setHeaderIncrementFixer(header);
        adapter.setShouldClipView(false);
        adapter.setParallaxHeader(header, recyclerView);
        adapter.setData(content);
        adapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((ViewHolder) viewHolder).textView.setText(adapter.getData().get(i));
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                final ViewHolder holder = new ViewHolder(getLayoutInflater().inflate(R.layout.row_recyclerview_cards, viewGroup, false));
                //don't set listeners on onBindViewHolder. For more info check http://androidshenanigans.blogspot.pt/2015/02/viewholder-pattern-common-mistakes.html
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "You clicked '" + adapter.getData().get(holder.getPosition() - (adapter.hasHeader() ? 1 : 0)) + "'", Toast.LENGTH_SHORT).show();
                    }
                });
                return holder;
            }

            @Override
            public int getItemCount() {
                return content.size();
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void createAdapter(RecyclerView recyclerView, final boolean differentType) {
        final List<String> content = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            content.add("item " + i);
        }
        final ParallaxRecyclerAdapter<String> adapter = new ParallaxRecyclerAdapter<>(content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View header = getLayoutInflater().inflate(R.layout.header, recyclerView, false);
        adapter.setParallaxHeader(header, recyclerView);
        adapter.setData(content);
        adapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
            final int VIEW_TYPE_1 = 0;
            final int VIEW_TYPE_2 = 1;

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                int itemViewType = getItemViewType(i);
                if (itemViewType == VIEW_TYPE_1)
                    ((ViewHolder) viewHolder).textView.setText(adapter.getData().get(i));
                else
                    ((DifferentViewHolder) viewHolder).imageView.setImageResource(R.drawable.wallpaper);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                if (i == VIEW_TYPE_1) {
                    final ViewHolder holder = new ViewHolder(getLayoutInflater().inflate(R.layout.row_recyclerview, viewGroup, false));
                    //don't set listeners on onBindViewHolder. For more info check http://androidshenanigans.blogspot.pt/2015/02/viewholder-pattern-common-mistakes.html
                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "You clicked '" + adapter.getData().get(holder.getPosition() - (adapter.hasHeader() ? 1 : 0)) + "'", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return holder;
                } else {
                    final DifferentViewHolder holder = new DifferentViewHolder(getLayoutInflater().inflate(R.layout.row_recyclerview_different, viewGroup, false));
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "You clicked '" + adapter.getData().get(holder.getPosition() - (adapter.hasHeader() ? 1 : 0)) + "'", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return holder;
                }
            }

            @Override
            public int getItemCount() {
                return content.size();
            }

            @Override
            public int getItemViewType(int position) {
                if (differentType) {
                    if (position % 2 == 0)
                        return VIEW_TYPE_2;

                }
                return VIEW_TYPE_1;
            }
        });
        recyclerView.setAdapter(adapter);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    static class DifferentViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public DifferentViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }


}
