package com.itec4860.dayplanner.sqliteDatabase;

/**
 * Class: Event
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 9, 2015.
 *
 * This class...
 *
 * Purpose:
 */
public class Event
{
    private long eventID;
    private String title;
    private String type;

    public Event(){}

    public Event(int eventID, String title, String type)
    {
        this.eventID = eventID;
        this.title = title;
        this.type = type;
    }

    public long getEventID()
    {
        return eventID;
    }

    public void setEventID(long eventID)
    {
        this.eventID = eventID;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
