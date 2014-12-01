package org.ligi.scr;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

    @InjectView(R.id.viewIndicatorImage)
    ImageView viewIndicatorImage;

    private CardView root;

    public EventViewHolder(CardView itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        this.root=itemView;
    }

    public void apply(final TalkPreferencesResponse response) {
        titleText.setText(response.getTitle());
        abstractText.setText(response.getAbstract());
        speakerText.setText(response.getSpeakers());
        trackText.setText(response.getTrackName());

        viewIndicatorImage.setVisibility(App.talkIds.getTalkIds().contains(Integer.valueOf(response.getEventId()))?View.VISIBLE:View.GONE);

        if (App.selectedEventId==null || response.getEventId()!=App.selectedEventId) {
            root.setCardElevation(root.getContext().getResources().getDimension(R.dimen.cardview_default_elevation));
        } else {
            root.setCardElevation(root.getContext().getResources().getDimension(R.dimen.cardview_default_elevation));root.setCardElevation(root.getContext().getResources().getDimension(R.dimen.cardview_elevation));
        }


        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.selectedEventId=response.getEventId();
                App.bus.post(new CurrentScopeChangeEvent());
            }
        });
    }

    public CardView getRoot() {
        return root;
    }
}
