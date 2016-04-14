package com.kamilslesinski.gridlayoutmanager.sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.kamilslesinski.gridlayoutmanager.DataSource;
import com.kamilslesinski.gridlayoutmanager.GridLayoutManager;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private RecyclerView mGridView;
    private RecyclerView mGridView2;
    private RecyclerView mGridView3;
    private GridLayoutManager mGridLayoutManager1;
    private GridLayoutManager mGridLayoutManager2;
    private GridLayoutManager mGridLayoutManager3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (RecyclerView) findViewById(R.id.grid_view);
        mGridView2 = (RecyclerView) findViewById(R.id.grid_view_2);
        mGridView3 = (RecyclerView) findViewById(R.id.grid_view_3);

        mTextView1 = (TextView) findViewById(R.id.textView1);
        mTextView2 = (TextView) findViewById(R.id.textView2);
        mTextView3 = (TextView) findViewById(R.id.textView3);

        initializeGridView1();
        initializeGridView2();
        initializeGridView3();
    }

    private void initializeGridView1() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mGridLayoutManager1 = new GridLayoutManager(dm);
        mGridLayoutManager1.setStripSize(48, dm);
        mGridLayoutManager1.setMaxStripLength(32000);
        mGridLayoutManager1.setResetListener(new GridLayoutManager.OnResetListener() {
            @Override
            public void onReset(int x, int y) {
                // resetting grid layout manager, i.e. data reloaded, etc.
            }
        });

        RecyclerView.RecycledViewPool rvc = new RecyclerView.RecycledViewPool();
        rvc.setMaxRecycledViews(0, 50);
        mGridView.setRecycledViewPool(rvc);
        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(mGridLayoutManager1);
        mGridView.setAdapter(new TestAdapter());
    }

    private void initializeGridView2() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mGridLayoutManager2 = new GridLayoutManager(dm);
        mGridLayoutManager2.setStripSize(48, dm);
        //mGridLayoutManager2.setMaxStripLength(200);
        mGridLayoutManager2.setDynamicStripLength(true);
        mGridLayoutManager2.setResetListener(new GridLayoutManager.OnResetListener() {
            @Override
            public void onReset(int x, int y) {
                // resetting grid layout manager, i.e. data reloaded, etc.
            }
        });

        RecyclerView.RecycledViewPool rvc = new RecyclerView.RecycledViewPool();
        rvc.setMaxRecycledViews(0, 50);
        mGridView2.setRecycledViewPool(rvc);
        mGridView2.setHasFixedSize(true);
        mGridView2.setLayoutManager(mGridLayoutManager2);
        mGridView2.setAdapter(new TestAdapter());
    }

    private void initializeGridView3() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mGridLayoutManager3 = new GridLayoutManager(dm);
        mGridLayoutManager3.setStripSize(80, dm);
        //mGridLayoutManager3.setMaxStripLength(200);
        mGridLayoutManager3.setDynamicStripLength(true);
        mGridLayoutManager3.setResetListener(new GridLayoutManager.OnResetListener() {
            @Override
            public void onReset(int x, int y) {
                // resetting grid layout manager, i.e. data reloaded, etc.
            }
        });

        RecyclerView.RecycledViewPool rvc = new RecyclerView.RecycledViewPool();
        rvc.setMaxRecycledViews(0, 50);
        mGridView3.setRecycledViewPool(rvc);
        mGridView3.setHasFixedSize(true);
        mGridView3.setLayoutManager(mGridLayoutManager3);
        mGridView3.setAdapter(new TestAdapter());
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateGridView1Data();
        updateGridView2Data();
        updateGridView3Data();
    }

    void updateGridView1Data() {
        RandomDataProvider1D dataProvider1D = new RandomDataProvider1D();
        DataSource<SimpleItem> dataSource = new DataSource<>();
        dataSource.setDataProvider(dataProvider1D);

        TestAdapter testAdapter = (TestAdapter) mGridView.getAdapter();
        testAdapter.setDataSource(dataSource);
        mGridLayoutManager1.setDataSource(dataSource);
        mTextView1.setText(String.format(Locale.US, "Item count: %d, Strip count: %d", dataSource.getItemCount(), dataSource.getStripsCount()));
    }

    void updateGridView2Data() {
        RandomDataProvider2D dataProvider2D = new RandomDataProvider2D();
        DataSource<SimpleItem> dataSource = new DataSource<>();
        dataSource.setDataProvider(dataProvider2D);

        TestAdapter testAdapter = (TestAdapter) mGridView2.getAdapter();
        testAdapter.setDataSource(dataSource);
        mGridLayoutManager2.setDataSource(dataSource);
        mTextView2.setText(String.format(Locale.US, "Item count: %d, Strip count: %d", dataSource.getItemCount(), dataSource.getStripsCount()));
    }

    void updateGridView3Data() {
        RandomDataProvider2D dataProvider2D = new RandomDataProvider2D();
        DataSource<SimpleItem> dataSource = new DataSource<>();
        dataSource.setDataProvider(dataProvider2D);

        TestAdapter testAdapter = (TestAdapter) mGridView3.getAdapter();
        testAdapter.setDataSource(dataSource);
        mGridLayoutManager3.setDataSource(dataSource);
        mGridLayoutManager3.setOrientation(GridLayoutManager.ORIENTATION_VERTICAL);
        mTextView3.setText(String.format(Locale.US, "Item count: %d, Strip count: %d", dataSource.getItemCount(), dataSource.getStripsCount()));
    }
}
