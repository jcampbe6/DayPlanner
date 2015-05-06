package com.itec4860.dayplanner.sqliteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class: SQLiteDBHandler
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: May 4, 2015
 * 
 * This class...
 * 
 * Purpose: 
 */
public class SQLiteDBHandler extends SQLiteOpenHelper
{
    // columns of the project table
    public static final String TABLE_PROJECT = "project";
    public static final String COLUMN_PROJECT_ID = "event_id";
    public static final String COLUMN_PROJECT_TITLE = "title";
    public static final String COLUMN_PROJECT_START_DATE = "start_date";
    public static final String COLUMN_PROJECT_END_DATE = "end_date";
    public static final String COLUMN_PROJECT_HAS_TASK = "has_task";

    // columns of the task table
    public static final String TABLE_TASK = "task";
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_TASK_PROJECT_ID = "event_id";
    public static final String COLUMN_TASK_TITLE = "title";
    public static final String COLUMN_TASK_DUE_DATE = "due_date";
    public static final String COLUMN_TASK_COMPLETED = "completed";

    private static final String DATABASE_NAME = "dayplanner";
    private static final int DATABASE_VERSION = 1;

    // SQL statement of the project table creation
    private static final String SQL_CREATE_TABLE_PROJECT = "CREATE TABLE " + TABLE_PROJECT + "("
            + COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PROJECT_TITLE + " TEXT NOT NULL, "
            + COLUMN_PROJECT_START_DATE + " TEXT NOT NULL, "
            + COLUMN_PROJECT_END_DATE + " TEXT NOT NULL, "
            + COLUMN_PROJECT_HAS_TASK + " INTEGER NOT NULL "
            +");";

    // SQL statement of the task table creation
    private static final String SQL_CREATE_TABLE_TASK = "CREATE TABLE " + TABLE_TASK + "("
            + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TASK_PROJECT_ID + " TEXT NOT NULL, "
            + COLUMN_TASK_TITLE + " TEXT NOT NULL, "
            + COLUMN_TASK_DUE_DATE + " TEXT NOT NULL, "
            + COLUMN_TASK_COMPLETED + " INTEGER NOT NULL "
            +");";

    public SQLiteDBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_TABLE_PROJECT);
        db.execSQL(SQL_CREATE_TABLE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // clear all data on database upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECT);

        // recreate the tables
        onCreate(db);
    }
}
