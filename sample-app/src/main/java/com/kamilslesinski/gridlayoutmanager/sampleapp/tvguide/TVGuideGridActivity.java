package com.kamilslesinski.gridlayoutmanager.sampleapp.tvguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.kamilslesinski.gridlayoutmanager.DataSource;
import com.kamilslesinski.gridlayoutmanager.GridLayoutManager;
import com.kamilslesinski.gridlayoutmanager.sampleapp.R;

public class TVGuideGridActivity extends AppCompatActivity {

    private RecyclerView mGridView;
    private RecyclerView mChannelList;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_guide);

        mGridView = (RecyclerView) findViewById(R.id.grid_view);
        mChannelList = (RecyclerView) findViewById(R.id.channel_list);
        initializeGridView();
        initializeChannelView();
    }

    private void initializeChannelView() {
        mChannelList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mChannelList.setAdapter(new ChannelAdapter());
    }

    private void initializeGridView() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mGridLayoutManager = new GridLayoutManager(dm);
        float stripSize = getResources().getDimension(R.dimen.tv_guide_strip_size);
        mGridLayoutManager.setStripSize((int) stripSize);
        //mGridLayoutManager.setMaxStripLength(32000);
        mGridLayoutManager.setDynamicStripLength(true);
        mGridLayoutManager.setResetListener(new GridLayoutManager.OnResetListener() {
            @Override
            public void onReset(int x, int y) {
                // resetting grid layout manager, i.e. data reloaded, etc.
            }
        });

        RecyclerView.RecycledViewPool rvc = new RecyclerView.RecycledViewPool();
        rvc.setMaxRecycledViews(0, 50);
        mGridView.setRecycledViewPool(rvc);
        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(mGridLayoutManager);
        mGridView.setAdapter(new TVGuideAdapter());
        mGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mChannelList.scrollBy(0, dy);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateGridViewData();
    }

    void updateGridViewData() {
        RandomTVGuideDataProvider2D dataProvider1D = new RandomTVGuideDataProvider2D();
        DataSource<TVGuideItem> dataSource = new DataSource<>();
        dataSource.setDataProvider(dataProvider1D);

        TVGuideAdapter testAdapter = (TVGuideAdapter) mGridView.getAdapter();
        testAdapter.setDataSource(dataSource);
        mGridLayoutManager.setDataSource(dataSource);

        ChannelAdapter channelAdapter = (ChannelAdapter) mChannelList.getAdapter();
        channelAdapter.setDataSource(dataSource);
    }
}
