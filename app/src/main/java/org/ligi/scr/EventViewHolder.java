package org.ligi.scr;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.metadude.java.library.halfnarp.model.TalkPreferencesResponse;

public class EventViewHolder extends RecyclerView.ViewHolder {


    @InjectView(R.id.titleTV)
    TextView titleText;


    @InjectView(R.id.abstractTV)
    TextView abstractText;

    @InjectView(R.id.speaker)
    TextView speakerText;

    public EventViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void apply(TalkPreferencesResponse response) {
        titleText.setText(response.getTitle());
        abstractText.setText(response.getAbstract());
        speakerText.setText(response.getSpeakers());
    }
}
