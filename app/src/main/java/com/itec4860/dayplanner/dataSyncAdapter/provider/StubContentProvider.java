package com.itec4860.dayplanner.dataSyncAdapter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Class: StubContentProvider
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: July 29, 2015
 * 
 * This class is a stub content provider.
 * 
 * Purpose: This class is required to use Android's sync adapter framework, however Day Planner
 * does not use a content provider.
 */
public class StubContentProvider extends ContentProvider
{

    /*
     * Always return true, indicating that the
     * provider loaded correctly.
     */
    @Override
    public boolean onCreate()
    {
        return true;
    }

    /*
     * query() always returns no results
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder)
    {
        return null;
    }

    /*
     * Return an empty String for MIME type
     */
    @Override
    public String getType(Uri uri)
    {
        return "";
    }

    /*
     * insert() always returns null (no URI)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        return null;
    }

    /*
     * delete() always returns "no rows affected" (0)
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        return 0;
    }

    /*
     * update() always returns "no rows affected" (0)
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }
}
