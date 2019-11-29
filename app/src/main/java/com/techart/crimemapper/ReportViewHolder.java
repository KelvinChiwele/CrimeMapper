package com.techart.crimemapper;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Recycler view holder for reports
 */
public final class ReportViewHolder extends RecyclerView.ViewHolder {
    public TextView tvSubject;
    public TextView tvParticularOfOffence;
    public TextView tvStatus;
    public TextView tvStation;
    public TextView date;
    public TextView tvDetails;

    public View mView;

    public ReportViewHolder(View itemView) {
        super(itemView);
        tvSubject = itemView.findViewById(R.id.tv_subject);
        tvParticularOfOffence = itemView.findViewById(R.id.tv_transit);
        tvStatus = itemView.findViewById(R.id.tv_status);
        tvStation = itemView.findViewById(R.id.tv_station);
        tvDetails = itemView.findViewById(R.id.tv_details);
        date = itemView.findViewById(R.id.tv_date);
        this.mView = itemView;
    }

}
