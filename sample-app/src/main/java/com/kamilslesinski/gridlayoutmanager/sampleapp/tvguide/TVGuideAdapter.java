package com.kamilslesinski.gridlayoutmanager.sampleapp.tvguide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kamilslesinski.gridlayoutmanager.DataSource;
import com.kamilslesinski.gridlayoutmanager.sampleapp.R;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/08
 */
public class TVGuideAdapter extends RecyclerView.Adapter<TVGuideViewHolder> {

    private DataSource<TVGuideItem> mDataSource;

    public void setDataSource(DataSource<TVGuideItem> dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public TVGuideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_guide_grid_item_layout, parent, false);
        return new TVGuideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TVGuideViewHolder holder, int position) {
        TVGuideItem gridItem = mDataSource.getItem(position);
        Context context = holder.itemView.getContext();
        holder.itemView.setTag(gridItem);
        holder.titleTextView.setText(context.getString(R.string.title_format, position % 5));
        int startTimeInMinutes = gridItem.getStartInMinutes();
        int startHours = startTimeInMinutes / 60;
        int startMinutes = startTimeInMinutes % 60;
        int endTimeInMinutes = gridItem.getEndInMinutes();
        int endHours = endTimeInMinutes / 60;
        int endMinutes = endTimeInMinutes % 60;
        holder.timeTextView.setText(context.getString(R.string.time_format, startHours, startMinutes, endHours, endMinutes));
    }

    @Override
    public int getItemCount() {
        return mDataSource != null ? mDataSource.getItemCount() : 0;
    }
}
