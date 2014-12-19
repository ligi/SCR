package org.ligi.scr.model;

import com.google.gson.annotations.SerializedName;

import org.apache.http.client.utils.CloneUtils;
import org.joda.time.Duration;

import java.util.HashMap;
import java.util.List;


public class Event {
    public String title;
    public String subtitle;

    public String date;

    public String duration;

    @SerializedName("abstract")
    public String abstractText;

    public String slug;

    public String logo;

    public String room;

    public String language;

    public List<Link> links;
    public List<Person> persons;
}
