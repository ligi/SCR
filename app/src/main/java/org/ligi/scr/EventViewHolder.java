package org.ligi.scr;

import android.support.v7.widget.CardView;
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

    @InjectView(R.id.track)
    TextView trackText;

    private CardView root;

    public EventViewHolder(CardView itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        this.root=itemView;
    }

    public void apply(TalkPreferencesResponse response) {
        titleText.setText(response.getTitle());
        abstractText.setText(response.getAbstract());
        speakerText.setText(response.getSpeakers());
        trackText.setText(response.getTrackName());
        root.setCardElevation(root.getContext().getResources().getDimension(R.dimen.cardview_default_elevation));

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.setCardElevation(root.getContext().getResources().getDimension(R.dimen.cardview_elevation));
            }
        });
    }

    public CardView getRoot() {
        return root;
    }
}
