package com.itec4860.dayplanner.sqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: ProjectDAO
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: May 04, 2015
 * 
 * This class is the Data Access Object for a Project event type.
 * 
 * Purpose: 
 */
public class ProjectDAO 
{
    // Database fields
    private SQLiteDatabase database;
    private SQLiteDBHandler dbHandler;
    private Context context;
    private String[] allColumns = {dbHandler.COLUMN_PROJECT_ID, dbHandler.COLUMN_PROJECT_TITLE, dbHandler.COLUMN_PROJECT_START_DATE,
            dbHandler.COLUMN_PROJECT_END_DATE, dbHandler.COLUMN_PROJECT_HAS_TASK};

    public ProjectDAO(Context context)
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

    public Project createProject(String title, String startDate, String endDate, int hasTask)
    {
        ContentValues values = new ContentValues();
        values.put(dbHandler.COLUMN_PROJECT_TITLE, title);
        values.put(dbHandler.COLUMN_PROJECT_START_DATE, startDate);
        values.put(dbHandler.COLUMN_PROJECT_END_DATE, endDate);
        values.put(dbHandler.COLUMN_PROJECT_HAS_TASK, hasTask);
        long insertId = database.insert(dbHandler.TABLE_PROJECT, null, values);
        Cursor cursor = database.query(dbHandler.TABLE_PROJECT, allColumns, dbHandler.COLUMN_PROJECT_ID +
                " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Project newProject = cursorToProject(cursor);
        cursor.close();

        return newProject;
    }

    public Project getProjectById(long id)
    {
        Cursor cursor = database.query(dbHandler.TABLE_PROJECT, allColumns, dbHandler.COLUMN_PROJECT_ID +
                " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursorToProject(cursor);
    }

    public List<Event> getAllProjectsByDate(String date)
    {
        List<Event> projectList = new ArrayList<>();
        Cursor cursor = database.query(dbHandler.TABLE_PROJECT, allColumns, dbHandler.COLUMN_PROJECT_START_DATE +
                " = ?", new String[]{date}, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Project project = cursorToProject(cursor);
            projectList.add(project);
            cursor.moveToNext();
        }

        cursor.close();

        return projectList;
    }

    public int getProjectCountByDate(String date)
    {
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = database.query(dbHandler.TABLE_PROJECT, allColumns, dbHandler.COLUMN_PROJECT_START_DATE +
                " = ?", new String[]{date}, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Project project = cursorToProject(cursor);
            projectList.add(project);
            cursor.moveToNext();
        }

        cursor.close();

        return projectList.size();
    }

    public boolean deleteProjectById(long id)
    {
        return database.delete(dbHandler.TABLE_PROJECT, dbHandler.COLUMN_PROJECT_ID + "=" + id, null) > 0;
    }

    protected Project cursorToProject(Cursor cursor)
    {
        Project project = new Project();
        project.setEventID(cursor.getLong(0));
        project.setTitle(cursor.getString(1));
        project.setStartDate(cursor.getString(2));
        project.setEndDate(cursor.getString(3));
        project.setHasTask(cursor.getInt(4));

        return project;
    }
}
