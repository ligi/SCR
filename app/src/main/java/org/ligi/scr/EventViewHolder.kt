package org.ligi.scr

import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import android.widget.FrameLayout
import info.metadude.java.library.halfnarp.model.GetTalksResponse
import kotlinx.android.synthetic.main.item_event.view.*
import org.joda.time.format.DateTimeFormat
import org.ligi.scr.model.Event
import org.ligi.scr.model.decorated.EventDecorator

class EventViewHolder(private val root: CardView) : RecyclerView.ViewHolder(root) {

    fun apply(response: Event) {

        val eventDecorator = EventDecorator(response)

        itemView.titleTV.text = response.title + response.room
        itemView.speaker.text = response.duration
        itemView.abstractTV.text = eventDecorator.start.toString(DateTimeFormat.shortTime()) + " " + eventDecorator.end.toString(DateTimeFormat.shortTime()) + " " + response.abstractText

        val main = 5 * eventDecorator.duration.standardMinutes

        root.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, main.toInt())
        root.requestLayout()
    }

    fun apply(response: GetTalksResponse, moveMethod: (Int, Int) -> Unit) {
        itemView.titleTV.text = response.title
        itemView.abstractTV.text = Html.fromHtml(response.abstract)
        itemView.abstractTV.movementMethod = LinkMovementMethod.getInstance()
        itemView.speaker.text = response.speakers


        itemView.yesButton.setOnClickListener {
            moveMethod.invoke(1, adapterPosition)
        }

        itemView.noButton.setOnClickListener {
            moveMethod.invoke(-1, adapterPosition)
        }

        itemView.shareView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, response.title)
            intent.putExtra(Intent.EXTRA_TEXT, response.abstract)
            intent.type = "text/plain"
            root.context.startActivity(intent)
        }
    }

}
