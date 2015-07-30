package com.itec4860.dayplanner.dataSyncAdapter.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Class: AuthenticatorService
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: July 29, 2015
 * 
 * This class...
 * 
 * Purpose: 
 */
public class AuthenticatorService extends Service
{
    // Instance field that stores the authenticator object
    private StubAccountAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new StubAccountAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
