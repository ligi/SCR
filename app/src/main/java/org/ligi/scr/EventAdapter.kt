package org.ligi.scr

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.ligi.scr.model.Event
import java.util.*

internal class EventAdapter(val events: ArrayList<Event>) : RecyclerView.Adapter<EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val res = inflater.inflate(R.layout.item_event, parent, false) as CardView
        return EventViewHolder(res)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) = holder.apply(events[position])

    override fun getItemCount() = events.size

}
