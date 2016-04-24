package com.kamilslesinski.gridlayoutmanager.sampleapp.generic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.kamilslesinski.gridlayoutmanager.DataSource;
import com.kamilslesinski.gridlayoutmanager.GridLayoutManager;
import com.kamilslesinski.gridlayoutmanager.sampleapp.R;

import java.util.Locale;

public class VerticalGridActivity extends AppCompatActivity {

    private TextView mTextView;
    private RecyclerView mGridView;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        mGridView = (RecyclerView) findViewById(R.id.grid_view);
        mTextView = (TextView) findViewById(R.id.textView);
        initializeGridView();
    }

    private void initializeGridView() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mGridLayoutManager = new GridLayoutManager(dm);
        mGridLayoutManager.setStripSize(48);
        mGridLayoutManager.setMaxStripLength(32000);
        mGridLayoutManager.setOrientation(GridLayoutManager.ORIENTATION_VERTICAL);
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
        mGridView.setAdapter(new TestAdapter());
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateGridViewData();
    }

    void updateGridViewData() {
        RandomDataProvider1D dataProvider1D = new RandomDataProvider1D();
        DataSource<SimpleItem> dataSource = new DataSource<>();
        dataSource.setDataProvider(dataProvider1D);

        TestAdapter testAdapter = (TestAdapter) mGridView.getAdapter();
        testAdapter.setDataSource(dataSource);
        mGridLayoutManager.setDataSource(dataSource);
        mTextView.setText(String.format(Locale.US, "Item count: %d, Strip count: %d", dataSource.getItemCount(), dataSource.getStripsCount()));
    }
}
