package org.ligi.scr;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.common.collect.Iterables;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.ligi.scr.model.Day;
import org.ligi.scr.model.Event;
import org.ligi.scr.model.decorated.EventDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListActivity extends ActionBarActivity {

    @InjectView(R.id.list_host)
    ViewGroup list_host;

    private List<RecyclerView> recyclers=new ArrayList<RecyclerView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getSupportActionBar().setIcon(R.drawable.logo);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(new DaySelector(this));

        getSupportActionBar().setSubtitle("Schedule Conflict Resolver");
        ButterKnife.inject(this);


        DateTime earliest_start = DateTime.parse(App.conference.days.get(0).date);

        for (Day day : App.conference.days) {
            if (DateTime.parse(day.date).isBefore(earliest_start)) {
                earliest_start = DateTime.parse(day.date);
            }
        }

        final HashMap<String, ArrayList<Event>> roomToAllEvents = new HashMap<>();


        final Set<String> rooms = App.conference.days.get(0).rooms.keySet();
        for (String room : rooms) {
            DateTime act_time = earliest_start;
            final ArrayList<Event> newEventList = new ArrayList<>();
            for (Day day : App.conference.days) {

                for (Event event : day.rooms.get(room)) {
                    final EventDecorator eventDecorator = new EventDecorator(event);

                    if (act_time.isBefore(eventDecorator.getStart())) {
                        final Event object = new Event();
                        object.title = "break";

                        object.date = act_time.toString(ISODateTimeFormat.dateTime());

                        final EventDecorator eventDecorator1 = new EventDecorator(object);

                        eventDecorator1.setEnd(eventDecorator.getStart());
                        newEventList.add(object);
                    }
                    act_time = eventDecorator.getEnd();
                    newEventList.add(event);
                }
            }
            roomToAllEvents.put(room, newEventList);
        }


        recyclers.clear();
        for (int i=0;i<rooms.size() ;i++) {
            final GridLayoutManager layoutManager1 = new GridLayoutManager(this, 1);
            RecyclerView recycler = (RecyclerView) getLayoutInflater().inflate(R.layout.recycler,list_host,false);
            recycler.setLayoutManager(layoutManager1);
            recycler.setAdapter((new EventAdapter(Iterables.get(roomToAllEvents.values(), i))));

            recyclers.add(recycler);
            recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getTag(R.id.tag_scroll_sync) != null) {
                        return;
                    }

                    for (RecyclerView recyclerView1 : recyclers) {
                        if (!recyclerView1.equals(recyclerView)) {
                            recyclerView1.setTag(R.id.tag_scroll_sync, true);
                            recyclerView1.scrollBy(dx, dy);
                            recyclerView1.setTag(R.id.tag_scroll_sync, null);
                        }
                    }
                }
            });
            list_host.addView(recycler);
        }

    }

    private void linkRecyclers(final RecyclerView... recyclerViews) {

        for (RecyclerView recyclerView : recyclerViews) {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getTag(R.id.tag_scroll_sync) != null) {
                        return;
                    }

                    for (RecyclerView recyclerView1 : recyclerViews) {
                        if (!recyclerView1.equals(recyclerView)) {
                            recyclerView1.setTag(R.id.tag_scroll_sync, true);
                            recyclerView1.scrollBy(dx, dy);
                            recyclerView1.setTag(R.id.tag_scroll_sync, null);
                        }
                    }
                }
            });
        }
    }
}