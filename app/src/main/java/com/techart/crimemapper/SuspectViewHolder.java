package com.techart.crimemapper;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Recycler view holder for reports
 */
public final class SuspectViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName;
    public TextView tvNrc;
    public TextView tvResidence;
    public TextView tvAge;

    public View mView;

    public SuspectViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
        tvNrc = itemView.findViewById(R.id.tv_nrc);
        tvResidence = itemView.findViewById(R.id.tv_residence);
        tvAge = itemView.findViewById(R.id.tv_age);
        this.mView = itemView;
    }

}
