package com.kamilslesinski.gridlayoutmanager.sampleapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamilslesinski.gridlayoutmanager.DataSource;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/08
 */
public class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {

    private DataSource<SimpleItem> mDataSource;

    public void setDataSource(DataSource<SimpleItem> dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);
        return new TestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        SimpleItem gridItem = mDataSource.getItem(position);

        holder.itemView.setTag(gridItem);
        holder.itemView.setBackgroundResource(
                position % 7 == 0 ? R.drawable.green_background : position % 4 == 0 ? R.drawable.red_background : R.drawable.blue_background);
        holder.titleTextView.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return mDataSource != null ? mDataSource.getItemCount() : 0;
    }
}
