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
public class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {

    private DataSource<TVGuideItem> mDataSource;

    public void setDataSource(DataSource<TVGuideItem> dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_guide_channel_item_layout, parent, false);
        return new ChannelHolder(v);
    }

    @Override
    public void onBindViewHolder(ChannelHolder holder, int position) {
        Context context = holder.itemView.getContext();
        holder.titleTextView.setText(context.getString(R.string.channel_format, position));
    }

    @Override
    public int getItemCount() {
        return mDataSource != null ? mDataSource.getStripsCount() : 0;
    }
}
