package com.poliveira.apps.parallaxrecycleradapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.poliveira.parallaxrecyclerview.HeaderLayoutManagerFixed;

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
        createAdapter(mRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isNormalAdapter) {
            createCardAdapter(mRecyclerView);
        } else {
            createAdapter(mRecyclerView);
        }
        isNormalAdapter = !isNormalAdapter;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private List<String> getContent() {
        List<String> content = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            content.add("item " + i);
        }
        return content;
    }

    private View getHeader(RecyclerView recyclerView) {
        return getLayoutInflater().inflate(R.layout.header, recyclerView, false);
    }

    private void createCardAdapter(RecyclerView recyclerView) {
        HeaderLayoutManagerFixed layoutManagerFixed = new HeaderLayoutManagerFixed(this);
        recyclerView.setLayoutManager(layoutManagerFixed);
        View header = getHeader(recyclerView);
        layoutManagerFixed.setHeaderIncrementFixer(header);
        ExampleParallaxRecyclerAdapter adapter = new ExampleParallaxRecyclerAdapter(getContent(), header, recyclerView, false, this, true);
        recyclerView.setAdapter(adapter);
    }

    private void createAdapter(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View header = getHeader(recyclerView);
        ExampleParallaxRecyclerAdapter adapter = new ExampleParallaxRecyclerAdapter(getContent(), header, recyclerView, true, this, false);
        recyclerView.setAdapter(adapter);
    }
}
