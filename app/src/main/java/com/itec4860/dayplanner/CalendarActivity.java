package com.itec4860.dayplanner;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**Class: CalendarActivity
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: March 6, 2015
 *
 * This class displays a screen representing a calendar of the current month determined by the
 * mobile device's local Calendar instance and allows the user to view the previous and next month
 * in relation to the current month displayed.
 *
 * Purpose: This class will serve as the user interface for the Day Planner application for viewing
 * the calendar, viewing calendar events, and creating and manipulating calendar events. This class
 * does not perform the functions related to viewing, creating and manipulating calendar events, but
 * only provides access to those functions.
 */

public class CalendarActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, View.OnClickListener
{
    private TextView currentMonthTitle;
    private ImageButton prevMonthButton;
    private ImageButton nextMonthButton;
    private GridView calendarGrid;
    private GridCellAdapter adapter;
    private Calendar calendar;
    private int day;
    private int month;
    private int year;
    private boolean navItemSelectedOnCreate;
    private final DateFormat dateFormatter = new DateFormat();
    private static final String DATE_FORMAT = "MMMM yyyy";

    // variables for saving and retrieving instance state data
    private static final String STATE_SELECTED_DAY = "selected_day";
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_month);

        navItemSelectedOnCreate = true;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.calendar_view_options_list, android.R.layout.simple_dropdown_item_1line);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);

        calendar = Calendar.getInstance(Locale.getDefault());
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        prevMonthButton = (ImageButton) this.findViewById(R.id.prevMonth);
        prevMonthButton.setOnClickListener(this);

        currentMonthTitle = (TextView) this.findViewById(R.id.currentMonthTitle);
        currentMonthTitle.setText(dateFormatter.format(DATE_FORMAT, calendar.getTime()));

        nextMonthButton = (ImageButton) this.findViewById(R.id.nextMonth);
        nextMonthButton.setOnClickListener(this);

        calendarGrid = (GridView) this.findViewById(R.id.calendarGrid);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SELECTED_DAY))
        {
            adapter = new GridCellAdapter(getApplicationContext(), day, month, year,
                    savedInstanceState.getInt(STATE_SELECTED_DAY));
            adapter.notifyDataSetChanged();
            calendarGrid.setAdapter(adapter);
        }

        else
        {
            adapter = new GridCellAdapter(getApplicationContext(), day, month, year);
            adapter.notifyDataSetChanged();
            calendarGrid.setAdapter(adapter);
        }
    }

    /**
     * Method: onCreateOptionsMenu
     * Creates a settings menu in the action bar. TODO: functional settings menu not implemented yet
     * @param menu the settings menu
     * @return true the menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    /**
     * Method: onOptionsItemSelected
     * Handles settings menu item clicks.
     * @param item the menu item clicked
     * @return true the action was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method: onNavigationItemSelected
     * Toggles between 'Month', 'Week', and 'Day' calendar layouts and opens a new calendar screen
     * based on the layout chosen. TODO: functional navigation menu not implemented yet
     * @param itemPosition the position in the list of the item selected
     * @param itemId the id of the item selected
     * @return true the action was handled
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {
        if (navItemSelectedOnCreate)
        {
            navItemSelectedOnCreate = false;
            return true;
        }

        String[] itemTitles = getResources().getStringArray(R.array.calendar_view_options_list);

        if (itemPosition < itemTitles.length)
        {
            Toast.makeText(getApplicationContext(), itemTitles[itemPosition], Toast.LENGTH_SHORT).show();
            return true;
        }

        return true;
    }

    /**
     * Method: onSaveInstanceState
     * Saves navigation dropdown menu data and calendar selected day data when the calendar view
     * is destroyed (screen orientation changes).
     * @param instanceState the reference to the calendar view's instance state
     */
    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        instanceState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
        instanceState.putInt(STATE_SELECTED_DAY, adapter.getSelectedDay());
    }

    /**
     * Method: onRestoreInstanceState
     * Restores the saved instance state data when the calendar view is recreated.
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM))
        {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState
                    .getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    /**
     * Method: setCalendarMonthYear
     * Sets the current calendar's month and year and updates the GridCellAdapter to the new month
     * and year to display the proper dates in each cell of the calendar.
     * @param month the new month
     * @param year the new year
     */
    private void setCalendarMonthYear(int month, int year)
    {
        adapter = new GridCellAdapter(getApplicationContext(), day, month, year);
        calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));
        currentMonthTitle.setText(dateFormatter.format(DATE_FORMAT, calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarGrid.setAdapter(adapter);
    }

    /**
     * Method: onClick
     * Responds to the 'next month' and 'previous month' buttons by setting the calendar month
     * and year to the corresponding previous or next month and year depending on which button was
     * clicked.
     * @param view the button view that was clicked
     */
    @Override
    public void onClick(View view)
    {
        if (view == prevMonthButton)
        {
            if (month == 1)
            {
                month = 12;
                year--;
            }

            else
            {
                month--;
            }

            setCalendarMonthYear(month, year);
        }

        if (view == nextMonthButton)
        {
            if (month == 12)
            {
                month = 1;
                year++;
            }

            else
            {
                month++;
            }

            setCalendarMonthYear(month, year);
        }
    }
}