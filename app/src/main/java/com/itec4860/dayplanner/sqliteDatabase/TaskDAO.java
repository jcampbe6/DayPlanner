package com.itec4860.dayplanner.sqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class: TaskDAO
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: May 04, 2015
 * 
 * This class...
 * 
 * Purpose: 
 */
public class TaskDAO 
{
    // Database fields
    private SQLiteDatabase database;
    private SQLiteDBHandler dbHandler;
    private Context context;
    private String[] allColumns = {dbHandler.COLUMN_TASK_ID, dbHandler.COLUMN_TASK_PROJECT_ID,
            dbHandler.COLUMN_TASK_TITLE, dbHandler.COLUMN_TASK_DUE_DATE, dbHandler.COLUMN_TASK_COMPLETED};

    public TaskDAO(Context context)
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

    public Task createTask(long projectID, String title, String dueDate, int completed)
    {
        ContentValues values = new ContentValues();
        values.put(dbHandler.COLUMN_TASK_PROJECT_ID, projectID);
        values.put(dbHandler.COLUMN_TASK_TITLE, title);
        values.put(dbHandler.COLUMN_TASK_DUE_DATE, dueDate);
        values.put(dbHandler.COLUMN_TASK_COMPLETED, completed);
        long insertId = database.insert(dbHandler.TABLE_TASK, null, values);
        Cursor cursor = database.query(dbHandler.TABLE_TASK, allColumns, dbHandler.COLUMN_TASK_ID +
                " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();

        return newTask;
    }

    public Task getTaskById(long id)
    {
        Cursor cursor = database.query(dbHandler.TABLE_TASK, allColumns, dbHandler.COLUMN_TASK_ID +
                " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursorToTask(cursor);
    }

    protected Task cursorToTask(Cursor cursor)
    {
        Task task = new Task();
        task.setTaskID(cursor.getLong(0));
        task.setProjectID(cursor.getLong(1));
        task.setTitle(cursor.getString(2));
        task.setDueDate(cursor.getString(3));
        task.setCompleted(cursor.getInt(4));

        return task;
    }
}
