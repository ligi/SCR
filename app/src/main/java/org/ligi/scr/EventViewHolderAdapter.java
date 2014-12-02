package org.ligi.scr;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import info.metadude.java.library.halfnarp.model.GetTalksResponse;

class EventViewHolderAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<GetTalksResponse> talkResponses;

    public EventViewHolderAdapter(List<GetTalksResponse> talkPreferencesResponses) {
        this.talkResponses = talkPreferencesResponses;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final CardView res = (CardView) inflater.inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(res);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.apply(talkResponses.get(position));
    }

    @Override
    public int getItemCount() {
        return talkResponses.size();
    }


    public GetTalksResponse findById(int id) {
        for (GetTalksResponse talkResponse : talkResponses) {
            if (talkResponse.getEventId() == id) {
                return talkResponse;
            }
        }

        return null;
    }
}
