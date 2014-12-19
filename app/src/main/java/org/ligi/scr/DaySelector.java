package org.ligi.scr;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.ligi.axt.adapters.SimpleStringAdapter;
import org.ligi.scr.model.Day;

import java.util.List;

public class DaySelector extends Spinner {
    public DaySelector(Context context) {
        super(context);

        final List<String> res=Lists.transform(App.conference.days,new Function<Day, String>() {
            @Override
            public String apply(Day input) {
                return "#"+App.conference.days.indexOf(input) + " " + input.date;
            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.day_spinner_item, android.R.id.text1,
                res.toArray(new String[res.size()]));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        setAdapter(adapter);
    }
}