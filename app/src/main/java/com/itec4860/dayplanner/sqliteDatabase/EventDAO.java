package com.itec4860.dayplanner.sqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class: EventDAO
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: May 04, 2015
 * 
 * This class is the Data Access Object for an event.
 * 
 * Purpose: 
 */
public class EventDAO 
{
    // Database fields
    private SQLiteDatabase database;
    private SQLiteDBHandler dbHandler;
    private Context context;
    private String[] allColumns = {dbHandler.COLUMN_EVENT_ID, dbHandler.COLUMN_EVENT_TITLE,
            dbHandler.COLUMN_EVENT_TYPE};

    public EventDAO(Context context)
    {
        this.context = context;
        dbHandler = new SQLiteDBHandler(context);

        // open the database
        try
        {
            open();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void open() throws SQLException
    {
        database = dbHandler.getWritableDatabase();
    }

    public void close()
    {
        dbHandler.close();
    }

    public Event createEvent(String title, String type)
    {
        ContentValues values = new ContentValues();
        values.put(dbHandler.COLUMN_EVENT_TITLE, title);
        values.put(dbHandler.COLUMN_EVENT_TYPE, type);
        long insertId = database.insert(dbHandler.TABLE_EVENT, null, values);
        Cursor cursor = database.query(dbHandler.TABLE_EVENT, allColumns, dbHandler.COLUMN_EVENT_ID +
        " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Event newEvent = cursorToEvent(cursor);
        cursor.close();

        return newEvent;
    }

    public Event getEventById(long id)
    {
        Cursor cursor = database.query(dbHandler.TABLE_EVENT, allColumns, dbHandler.COLUMN_EVENT_ID +
        " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursorToEvent(cursor);
    }

    public void deleteEvent(Event event)
    {
        long id = event.getEventID();
        // delete all events with projects and tasks
//        ProjectDAO projecttDAO = new ProjectDAO(context);
//        List<Project> projectList = projectDAO.getEventProjects(id);
    }

    protected Event cursorToEvent(Cursor cursor)
    {
        Event event = new Event();
        event.setEventID(cursor.getLong(0));
        event.setTitle(cursor.getString(1));
        event.setType(cursor.getString(2));

        return event;
    }
}
