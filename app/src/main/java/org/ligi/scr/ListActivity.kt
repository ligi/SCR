package org.ligi.scr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.ligi.scr.model.Event
import org.ligi.scr.model.decorated.EventDecorator
import java.util.*

class ListActivity : AppCompatActivity() {

    private val recyclers = ArrayList<RecyclerView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_list)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = DaySelector(this)

        supportActionBar?.subtitle = "Schedule Conflict Resolver"

        var earliestEventTime = DateTime.parse(App.conference.days[0].date).plusDays(1)
        var latestEventTime = DateTime.parse(App.conference.days[0].date)

        for (day in App.conference.days) {
            for (events in day.rooms.values) {
                for (event in events) {
                    val dateTime = DateTime(event.date)
                    if (dateTime.isBefore(earliestEventTime)) {
                        earliestEventTime = dateTime
                    }
                    if (dateTime.isAfter(latestEventTime)) {
                        latestEventTime = dateTime
                    }
                }
            }
        }

        val roomToAllEvents = HashMap<String, ArrayList<Event>>()

        val rooms = App.conference.days[0].rooms.keys
        for (room in rooms) {
            var act_time = earliestEventTime
            val newEventList = ArrayList<Event>()
            for (day in App.conference.days) {

                day.rooms[room]?.forEach {
                    val eventDecorator = EventDecorator(it)

                    if (act_time.isBefore(eventDecorator.start)) {
                        val breakEvent = Event()
                        breakEvent.title = "break"
                        breakEvent.date = act_time.toString(ISODateTimeFormat.dateTime())

                        val eventDecorator1 = EventDecorator(breakEvent)

                        eventDecorator1.end = eventDecorator.start
                        newEventList.add(breakEvent)
                    }
                    act_time = eventDecorator.end
                    newEventList.add(it)
                }
            }

            if (DateTime.parse(newEventList.last().date).isBefore(latestEventTime)) {
                val event = Event()
                event.title = "end"
                event.date = DateTime.parse(newEventList.last().date).toString(ISODateTimeFormat.dateTime())
                EventDecorator(event).end = latestEventTime
                newEventList.add(event)
            }
            roomToAllEvents.put(room, newEventList)
        }


        recyclers.clear()
        for (i in rooms.indices) {
            val layoutManager1 = GridLayoutManager(this, 1)
            val recycler = layoutInflater.inflate(R.layout.recycler, list_host, false) as RecyclerView
            recycler.layoutManager = layoutManager1
            recycler.adapter = EventAdapter(roomToAllEvents.values.elementAt(i))

            recyclers.add(recycler)
            recycler.setOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    if (recyclerView!!.getTag(R.id.tag_scroll_sync) != null) {
                        return
                    }

                    for (recyclerView1 in recyclers) {
                        if (recyclerView1 != recyclerView) {
                            recyclerView1.setTag(R.id.tag_scroll_sync, true)
                            recyclerView1.scrollBy(dx, dy)
                            recyclerView1.setTag(R.id.tag_scroll_sync, null)
                        }
                    }
                }
            })
            list_host!!.addView(recycler)
        }

    }

}