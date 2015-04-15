package com.itec4860.dayplanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**
         * Registration status will be saved in the user's preferences as a boolean 'isUserRegistered',
         * and will be retrieved for verification.
         */
        SharedPreferences dayPlannerSettings = getSharedPreferences("dayPlannerSettings", MODE_PRIVATE);
        boolean isUserRegistered = dayPlannerSettings.getBoolean("isUserRegistered", false);

        if (isUserRegistered)
        {
            Intent viewCalendarIntent = new Intent(getApplicationContext(), CalendarActivity.class);
            startActivity(viewCalendarIntent);
            finish();
        }

        else
        {
            Intent registrationIntent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(registrationIntent);
            finish();
        }
    }

}
