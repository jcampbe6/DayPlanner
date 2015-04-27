package com.itec4860.dayplanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**Class: CalendarActivity
 * @author Joshua Campbell
 * @version 1.1
 * Course: ITEC 4860 Spring 2015
 * Written: March 6, 2015
 *
 * This class displays a screen representing a calendar of the current month determined by the
 * mobile device's local Calendar instance and allows the user to view the previous and next months
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
    private CalendarGridAdapter adapter;
    private int oldDay;
    private int currentDay;
    private int month;
    private int year;
    private String currentDate;
    private String selectedDate;

    private final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private final int[] DAYS_OF_THE_MONTHS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    // variables for saving and retrieving instance state data
    private static final String STATE_MONTH = "month";
    private static final String STATE_YEAR = "year";
    private static final String STATE_SELECTED_DATE = "selected_date";
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_month);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.toggle_calendar_options_list, R.layout.action_bar_dropdown_item);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);

        // sets spinner selection to 'Month'
        actionBar.setSelectedNavigationItem(0);

        prevMonthButton = (ImageButton) this.findViewById(R.id.prevMonth);
        prevMonthButton.setOnClickListener(this);
        nextMonthButton = (ImageButton) this.findViewById(R.id.nextMonth);
        nextMonthButton.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_MONTH))
        {
            month = savedInstanceState.getInt(STATE_MONTH);
            year = savedInstanceState.getInt(STATE_YEAR);
            updateCurrentDay();
        }

        else
        {
            setCalendarToCurrentDate();
        }

        currentMonthTitle = (TextView) this.findViewById(R.id.currentMonthTitle);
        setCalendarTitle();

        calendarGrid = (GridView) this.findViewById(R.id.calendarGrid);
        registerForContextMenu(calendarGrid);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SELECTED_DATE))
        {
            selectedDate = savedInstanceState.getString(STATE_SELECTED_DATE);
            adapter = new CalendarGridAdapter(getApplicationContext(), currentDate,selectedDate);
        }

        else
        {
            adapter = new CalendarGridAdapter(getApplicationContext(), currentDate);
        }

        fillCalendar();
        adapter.notifyDataSetChanged();
        calendarGrid.setAdapter(adapter);

        calendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DateInfoHolder dateInfoHolder = (DateInfoHolder) view.getTag();
                markSelectedDate(dateInfoHolder.getDate());

                // TODO - for testing
                Toast.makeText(getApplicationContext(), dateInfoHolder.getDate(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method: setCalendarToCurrentDate
     * Sets the month, day, and year for the current month based on the device's internal calendar.
     */
    private void setCalendarToCurrentDate()
    {
        Calendar localDeviceCalendar = Calendar.getInstance(Locale.getDefault());
        month = localDeviceCalendar.get(Calendar.MONTH) + 1;
        currentDay = localDeviceCalendar.get(Calendar.DAY_OF_MONTH);
        year = localDeviceCalendar.get(Calendar.YEAR);
        currentDate = month + "/" + currentDay + "/" + year;
    }

    /**
     * Method: updateCurrentDay
     * Updates the current day for the app's calendar using the mobile device's internal calendar
     * instance.
     */
    private void updateCurrentDay()
    {
        oldDay = currentDay;
        Calendar localDeviceCalendar = Calendar.getInstance(Locale.getDefault());
        currentDay = localDeviceCalendar.get(Calendar.DAY_OF_MONTH);
        currentDate = (localDeviceCalendar.get(Calendar.MONTH)+ 1) + "/" + currentDay
                + "/" + localDeviceCalendar.get(Calendar.YEAR);
    }

    /**
     * Method: markSelectedDate
     * Highlights the selected date.
     * @param date the selected date to highlight
     */
    private void markSelectedDate(String date)
    {
        selectedDate = date;
        adapter.setSelectedDate(date);
        adapter.notifyDataSetChanged();
    }

    /**
     * Method: fillCalendar
     * Fills the calendar grid with cells representing each day in a month in a given year by
     * adding the dates of the last days of the previous month leading into the first week of
     * the current month, adding each day of the current month, adding the first days of the next
     * month that trail out of the last week of the current month, assigning each date a text color,
     * and adding each date to the calendar grid adapter that actually displays the calendar.
     */
    private void fillCalendar()
    {
        int daysInPrevMonth;
        int prevMonth;
        int yearOfPrevMonth;
        int nextMonth;
        int yearOfNextMonth;
        int daysInCurrentMonth = getMonthTotalDays(month);

        // adjusted currentMonthNum in parameters by -1 for GregorianCalendar compatibility
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month - 1, 1);

        if (month == 12)
        {
            prevMonth = month - 1;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
            nextMonth = 1;
            yearOfPrevMonth = year;
            yearOfNextMonth = year + 1;
        }

        else if (month == 1)
        {
            prevMonth = 12;
            yearOfPrevMonth = year - 1;
            yearOfNextMonth = year;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
            nextMonth = 2;
        }

        else
        {
            prevMonth = month - 1;
            nextMonth = month + 1;
            yearOfNextMonth = year;
            yearOfPrevMonth = year;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
        }

        if (gregorianCalendar.isLeapYear(year) && month == 2)
        {
            daysInCurrentMonth++;
        }

        int numOfLeadingDays = gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1;

        //calculate the 'day of the month' for each day of the previous month that leads into the
        //first week of the current month and adds info for those days to the cell info list
        for (int i = 1; i <= numOfLeadingDays; i++)
        {
            int leadingDayOfMonth = daysInPrevMonth - numOfLeadingDays + i;

            adapter.addItem(new DateInfoHolder(String.valueOf(prevMonth), String.valueOf(leadingDayOfMonth),
                    String.valueOf(yearOfPrevMonth), getApplicationContext().getResources()
                    .getColor(R.color.nextAndPrevMonthDateColor)));
        }

        //add info for each day of the current month to the cell info list
        for (int i = 1; i <= daysInCurrentMonth; i++)
        {
            adapter.addItem(new DateInfoHolder(String.valueOf(month), String.valueOf(i),
                    String.valueOf(year), getApplicationContext().getResources()
                    .getColor(R.color.currentMonthDateColor)));
        }

        //add info for each day of the next month that trails out of the last week of the current
        //month to the cell info list
        for (int i = 0; i < adapter.getCount() % 7; i++)
        {
            adapter.addItem(new DateInfoHolder(String.valueOf(nextMonth), String.valueOf(i + 1),
                    String.valueOf(yearOfNextMonth), getApplicationContext().getResources()
                    .getColor(R.color.nextAndPrevMonthDateColor)));
        }
    }

    /**
     * Method: onCreateOptionsMenu
     * Creates a settings menu in the action bar.
     * @param menu the settings menu
     * @return true the menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    /**
     * Method: onOptionsItemSelected
     * Handles settings menu item selection actions to share the calendar or to display national
     * holidays.
     * @param item the menu item selected
     * @return true the action was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //TODO: handle settings menu selections here
        int id = item.getItemId();

        if (id == R.id.shareCalendar)
        {
            Toast.makeText(getApplicationContext(), "Share Calendar", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.displayNationalHolidays)
        {
            Toast.makeText(getApplicationContext(), "Display National Holidays", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.goToCurrentDate)
        {
            setCalendarToCurrentDate();
            updateCalendarUI();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method: onNavigationItemSelected
     * Toggles between 'Month', 'Week', and 'Day' calendar layouts and opens a new calendar screen
     * based on the layout chosen.
     * @param itemPosition the position in the list of the item selected
     * @param itemId the id of the item selected
     * @return true the action was handled
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {
        //TODO: handle calendar view drop down menu selections here

        Intent thisActivity = getIntent();

        if (thisActivity.hasExtra("prevActivityPosition") &&
                Integer.parseInt(thisActivity.getStringExtra("prevActivityPosition")) == itemPosition)
        {
            onBackPressed();
        }

        else if (itemPosition == 1)
        {
            Intent weekIntent = new Intent(getApplicationContext(), CalendarActivityWeekly.class);
            weekIntent.putExtra("prevActivityPosition", "0");
            startActivity(weekIntent);
        }

        else if (itemPosition == 2)
        {
            Toast.makeText(getApplicationContext(), "Day", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    /**
     * Method: onCreateContextMenu
     * Opens a pop up menu when a date is long pressed and highlights the selected date. Menu will
     * contain options to view events related to the selected date or to add a new event to the
     * selected date.
     * @param menu the pop up menu
     * @param view the date that was selected
     * @param menuInfo the info about the selected date
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        DateInfoHolder dateInfoHolder = adapter.getItem(info.position);
        markSelectedDate(dateInfoHolder.getDate());

        menu.setHeaderTitle(dateInfoHolder.getDate());

        MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.selected_date_options_menu, menu);
    }

    /**
     * Method: onContextItemSelected
     * Handles context menu item selection actions to either view the selected event or to create a
     * new event.
     * @param item the menu item that was selected
     * @return true the action was handled
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        // TODO: handle 'add event' here
        int id = item.getItemId();

        if (id == R.id.addEvent)
        {
            Intent addEventIntent = new Intent(getApplicationContext(), EventViewActivity.class);
            addEventIntent.putExtra("date", selectedDate);
            startActivity(addEventIntent);
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
        instanceState.putInt(STATE_MONTH, month);
        instanceState.putInt(STATE_YEAR, year);
        instanceState.putString(STATE_SELECTED_DATE, adapter.getSelectedDate());
    }

    /**
     * Method: onRestoreInstanceState
     * Restores the saved instance state data when the calendar view is recreated.
     * @param savedInstanceState the saved state of the app instance
     */
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM))
        {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState
                    .getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    /**
     * Method: onPostResume
     * Updates calendar data after app has been minimized and then resumed.
     */
    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        updateCurrentDay();

        if (currentDay != oldDay)
        {
            adapter = new CalendarGridAdapter(getApplicationContext(), currentDate);
            fillCalendar();
            adapter.notifyDataSetChanged();
            calendarGrid.setAdapter(adapter);
        }
    }

    /**
     * Method: updateCalendarUI
     * Updates the calendar user interface to reflect any changes in the month or year.
     */
    private void updateCalendarUI()
    {
        updateCurrentDay();
        adapter = new CalendarGridAdapter(getApplicationContext(), currentDate);
        setCalendarTitle();
        fillCalendar();
        adapter.notifyDataSetChanged();
        calendarGrid.setAdapter(adapter);
    }

    /**
     * Method: getMonthName
     * Returns the name of a month that is specified by the month number.
     * @param monthNumber the number of the desired month
     * @return the name of the month
     */
    private String getMonthName(int monthNumber)
    {
        return MONTHS[monthNumber - 1];
    }

    /**
     * Method: getMonthTotalDays
     * Returns the total number of days in a month specified by the month number.
     * @param monthNumber the number of the desired month
     * @return the number of days
     */
    private int getMonthTotalDays(int monthNumber)
    {
        return DAYS_OF_THE_MONTHS[monthNumber - 1];
    }

    /**
     * Method: setCalendarTitle
     * Formats and sets the calendar title.
     */
    private void setCalendarTitle()
    {
        currentMonthTitle.setText(getMonthName(month) + " " + year);
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

            updateCalendarUI();
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

            updateCalendarUI();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getSupportActionBar().setSelectedNavigationItem(0); // set spinner to 'Month'
    }
}