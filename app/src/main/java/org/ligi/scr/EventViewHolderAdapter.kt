package org.ligi.scr

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import info.metadude.java.library.halfnarp.model.GetTalksResponse

internal class EventViewHolderAdapter(private val talkResponses: List<GetTalksResponse>) : RecyclerView.Adapter<EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val res = inflater.inflate(R.layout.item_event, parent, false) as CardView
        return EventViewHolder(res)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) = holder.apply(talkResponses[position])

    override fun getItemCount() = talkResponses.size

}

