package com.kamilslesinski.gridlayoutmanager.sampleapp.tvguide;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kamilslesinski.gridlayoutmanager.sampleapp.R;

/**
 * @author Kamil Ślesiński (slesinskik@gmail.com)
 * @since 2016/04/08
 */
public class ChannelHolder extends RecyclerView.ViewHolder {
    public TextView titleTextView;

    public ChannelHolder(View itemView) {
        super(itemView);

        titleTextView = (TextView) itemView.findViewById(R.id.title);
    }
}
