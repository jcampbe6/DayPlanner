package com.itec4860.dayplanner.dataSyncAdapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Class: SyncAdapter
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: July 30, 2015
 * 
 * This class is a data sync adapter.
 * 
 * Purpose: Handles the transfer of data between a server and an
 * app using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter
{

    /**
     * Constructor: SyncAdapter
     * Sets up the sync adapter.
     * @param context
     * @param autoInitialization
     */
    public SyncAdapter(Context context, boolean autoInitialization)
    {
        super(context, autoInitialization);
    }

    /**
     * Constructor: SyncAdapter
     * Sets up the sync adapter. This form of the constructor maintains compatibility with
     * Android 3.0 and later platform versions.
     * @param context
     * @param autoInitialization
     * @param allowParallelSyncs
     */
    public SyncAdapter(Context context, boolean autoInitialization, boolean allowParallelSyncs)
    {
        super(context,autoInitialization,allowParallelSyncs);
    }

    /**
     * Method: onPerformSync
     * Saves data to internal SQLite database and performs data synchronization between web server
     * and app.
     * @param account
     * @param extras
     * @param authority
     * @param provider
     * @param syncResult
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult)
    {
        //TODO: add network-related and data sync code here
    }
}
