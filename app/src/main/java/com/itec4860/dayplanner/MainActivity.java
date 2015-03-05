package com.itec4860.dayplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**Class: MainActivity
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: March 5, 2015
 *
 * This class will check if a user registration has occurred on this application.
 * If a user registration has not occurred, then it will switch to a registration screen.
 * If a user registration has occurred, then it will switch to the default calendar screen.
 *
 * Purpose: This class is the initial class that is started when the Day Planner
 * mobile device application is first initiated by a user of the application.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check for registration status
        //then switch to the appropriate screen/activity

        //setContentView(R.layout.activity_main);

        Intent registrationIntent = new Intent(getApplicationContext(), CalendarActivity.class);
        startActivity(registrationIntent);
        finish();
    }

}
