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

/**Class: CalendarActivityWeek
 * @author Joshua Campbell
 * @version 1.0
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

public class CalendarActivityWeekly extends ActionBarActivity implements ActionBar.OnNavigationListener, View.OnClickListener
{
    private TextView currentMonthTitle;
    private ImageButton prevWeekButton;
    private ImageButton nextWeekButton;
    private GridView calendarGridWeek;
    private CalendarGridAdapter adapter;
    private int oldDay;
    private int currentDay;
    private int month;
    private int week;
    private int year;
    private String currentDate;

    private final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private final int[] DAYS_OF_THE_MONTHS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    // variables for saving and retrieving instance state data
    private static final String STATE_MONTH = "month";
    private static final String STATE_WEEK = "week";
    private static final String STATE_YEAR = "year";
    private static final String STATE_SELECTED_DAY = "selected_day";
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_weekly);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.toggle_calendar_options_list, R.layout.action_bar_dropdown_item);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);

        // sets spinner selection to 'Week'
        actionBar.setSelectedNavigationItem(1);

        prevWeekButton = (ImageButton) this.findViewById(R.id.prevWeek);
        prevWeekButton.setOnClickListener(this);
        nextWeekButton = (ImageButton) this.findViewById(R.id.nextWeek);
        nextWeekButton.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_WEEK))
        {
            month = savedInstanceState.getInt(STATE_MONTH);
            year = savedInstanceState.getInt(STATE_YEAR);
            week = savedInstanceState.getInt(STATE_WEEK);
            updateCurrentDay();
        }

        else
        {
            setCalendarToCurrentDate();
        }

        currentMonthTitle = (TextView) this.findViewById(R.id.currentMonthTitle);
        setCalendarTitle();

        calendarGridWeek = (GridView) this.findViewById(R.id.calendarGridWeek);
        registerForContextMenu(calendarGridWeek);


        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SELECTED_DAY))
        {
            adapter = new CalendarGridAdapter(getApplicationContext(), currentDate,
                    savedInstanceState.getString(STATE_SELECTED_DAY));
        }

        else
        {
            adapter = new CalendarGridAdapter(getApplicationContext(), currentDate);
        }

        fillCalendarWeek(month, year);
        adapter.notifyDataSetChanged();
        calendarGridWeek.setAdapter(adapter);

        calendarGridWeek.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DateInfoHolder dateInfoHolder = (DateInfoHolder) view.getTag();
                markSelectedDate(dateInfoHolder.getDate());

                // TODO: for testing
                Toast.makeText(getApplicationContext(), dateInfoHolder.getDate(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method: setCalendarToCurrentDate
     * Sets the month, day, and year for the current month.
     */
    private void setCalendarToCurrentDate()
    {
        Calendar localDeviceCalendar = Calendar.getInstance(Locale.getDefault());
        month = localDeviceCalendar.get(Calendar.MONTH) + 1;
        currentDay = localDeviceCalendar.get(Calendar.DAY_OF_MONTH);
        week = localDeviceCalendar.get(Calendar.WEEK_OF_YEAR);
        year = localDeviceCalendar.get(Calendar.YEAR);
        currentDate = getMonthName(month) + " " + currentDay + ", " + year;
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
        currentDate = getMonthName(localDeviceCalendar.get(Calendar.MONTH)+ 1) + " " + currentDay
                + ", " + localDeviceCalendar.get(Calendar.YEAR);
    }

    /**
     * Method: markSelectedDate
     * Highlights the selected date.
     * @param date the selected date to highlight
     */
    private void markSelectedDate(String date)
    {
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
     * @param currentMonthNum the current or specified month
     * @param currentYear the current of specified year
     */
/*    private void fillCalendar(int currentDayOfMonth, int currentMonthNum, int currentYear)
    {
        int daysInPrevMonth;
        int prevMonth;
        int yearOfPrevMonth;
        int nextMonth;
        int yearOfNextMonth;
        int daysInCurrentMonth = getMonthTotalDays(currentMonthNum);

        final String TODAY_DATE_COLOR = "white";
        final String CURRENT_MONTH_DATE_COLOR = "black";
        final String NEXT_AND_PREV_MONTH_DATE_COLOR = "lightgrey";

        // adjusted currentMonthNum by -1 for GregorianCalendar compatibility
        GregorianCalendar gregorianCalendar = new GregorianCalendar(currentYear, currentMonthNum - 1, 1);

        if (currentMonthNum == 12)
        {
            prevMonth = currentMonthNum - 1;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
            nextMonth = 1;
            yearOfPrevMonth = currentYear;
            yearOfNextMonth = currentYear + 1;
        }

        else if (currentMonthNum == 1)
        {
            prevMonth = 12;
            yearOfPrevMonth = currentYear - 1;
            yearOfNextMonth = currentYear;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
            nextMonth = 2;
        }

        else
        {
            prevMonth = currentMonthNum - 1;
            nextMonth = currentMonthNum + 1;
            yearOfNextMonth = currentYear;
            yearOfPrevMonth = currentYear;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
        }

        if (gregorianCalendar.isLeapYear(currentYear) && currentMonthNum == 2)
        {
            daysInCurrentMonth++;
        }

        int numOfLeadingDays = gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1;

        //calculate the 'day of the month' for each day of the previous month that leads into the
        //first week of the current month and add info for those days to the cell info list
        for (int i = 1; i <= numOfLeadingDays; i++)
        {
            int leadingDayOfMonth = daysInPrevMonth - numOfLeadingDays + i;

            adapter.addItem(new DateInfoHolder(getMonthName(prevMonth), String.valueOf(leadingDayOfMonth),
                    String.valueOf(yearOfPrevMonth), NEXT_AND_PREV_MONTH_DATE_COLOR));
        }

        //add info for each day of the current month to the cell info list
        for (int i = 1; i <= daysInCurrentMonth; i++)
        {
            if (i == currentDayOfMonth)
            {
                adapter.addItem(new DateInfoHolder(getMonthName(currentMonthNum), String.valueOf(i),
                        String.valueOf(currentYear), TODAY_DATE_COLOR));
            }

            else
            {
                adapter.addItem(new DateInfoHolder(getMonthName(currentMonthNum), String.valueOf(i),
                        String.valueOf(currentYear), CURRENT_MONTH_DATE_COLOR));
            }
        }

        //add info for each day of the next month that trails out of the last week of the current
        //month to the cell info list
        for (int i = 0; i < adapter.getCount() % 7; i++)
        {
            adapter.addItem(new DateInfoHolder(getMonthName(nextMonth), String.valueOf(i + 1),
                    String.valueOf(yearOfNextMonth), NEXT_AND_PREV_MONTH_DATE_COLOR));
        }
    }*/
    private void fillCalendarWeek(int currentMonthNum, int currentYear)
    {
        int daysInPrevMonth;
        int prevMonth;
        int yearOfPrevMonth;
        int nextMonth;
        int yearOfNextMonth;
        int daysInCurrentMonth = getMonthTotalDays(currentMonthNum);

        int currentWeek;
        int currentWeekMonth;
        int currentJulianDay;
        int dayNum;
        int endOfWeekDay = 1;
        int startOfWeekDay = 31;

        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonthNum -1, currentDay);
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        currentWeekMonth = cal.get(Calendar.WEEK_OF_MONTH);
        currentJulianDay = cal.get(Calendar.DAY_OF_YEAR);

        // find start and end week days for the month
        for (int i = 1; i <= 31; i++)
        {
            cal.set(currentYear, currentMonthNum - 1, i);
            if (currentWeek == cal.get(Calendar.WEEK_OF_YEAR))
            {
                dayNum = cal.get(Calendar.DAY_OF_MONTH);

                if (dayNum > endOfWeekDay)
                {
                    endOfWeekDay = dayNum;
                }

                if (dayNum < startOfWeekDay)
                {
                    startOfWeekDay = dayNum;
                }
            }
        }

        // adjusted currentMonthNum by -1 for GregorianCalendar compatibility
        GregorianCalendar gregorianCalendar = new GregorianCalendar(currentYear, currentMonthNum - 1, 1);

        if (currentMonthNum == 12)
        {
            prevMonth = currentMonthNum - 1;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
            nextMonth = 1;
            yearOfPrevMonth = currentYear;
            yearOfNextMonth = currentYear + 1;
        }

        else if (currentMonthNum == 1)
        {
            prevMonth = 12;
            yearOfPrevMonth = currentYear - 1;
            yearOfNextMonth = currentYear;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
            nextMonth = 2;
        }

        else
        {
            prevMonth = currentMonthNum - 1;
            nextMonth = currentMonthNum + 1;
            yearOfNextMonth = currentYear;
            yearOfPrevMonth = currentYear;
            daysInPrevMonth = getMonthTotalDays(prevMonth);
        }

        if (gregorianCalendar.isLeapYear(currentYear) && currentMonthNum == 2)
        {
            daysInCurrentMonth++;
        }

        int numOfLeadingDays = gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1;

        //calculate the 'day of the month' for each day of the previous month that leads into the
        //first week of the current month and add info for those days to the cell info list
        if (currentWeekMonth == 1 && (endOfWeekDay - startOfWeekDay) < 6) {
            for (int i = 1; i <= numOfLeadingDays; i++) {
                int leadingDayOfMonth = daysInPrevMonth - numOfLeadingDays + i;

                adapter.addItem(new DateInfoHolder(getMonthName(prevMonth), String.valueOf(leadingDayOfMonth),
                        String.valueOf(yearOfPrevMonth), getApplicationContext().getResources()
                        .getColor(R.color.nextAndPrevMonthDateColor)));
            }
        }

        //add info for each day of the current month to the cell info list
        for (int i = 1; i <= daysInCurrentMonth; i++) {
        if (i >= startOfWeekDay && i <= endOfWeekDay) {

            adapter.addItem(new DateInfoHolder(getMonthName(currentMonthNum), String.valueOf(i),
                    String.valueOf(currentYear), getApplicationContext().getResources()
                    .getColor(R.color.currentMonthDateColor)));

        }
    }

        //add info for each day of the next month that trails out of the last week of the current
        //month to the cell info list
        if (currentWeekMonth == 5 && (endOfWeekDay - startOfWeekDay) < 6) {
            for (int i = 0; i < adapter.getCount() % 7; i++) {
                adapter.addItem(new DateInfoHolder(getMonthName(nextMonth), String.valueOf(i + 1),
                        String.valueOf(yearOfNextMonth), getApplicationContext().getResources()
                        .getColor(R.color.nextAndPrevMonthDateColor)));
            }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.shareCalendar)
        {
            return true;
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

        else if (itemPosition == 0)
        {
            Intent monthIntent = new Intent(getApplicationContext(), CalendarActivity.class);
            monthIntent.putExtra("prevActivityPosition", "1");
            startActivity(monthIntent);
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
        instanceState.putInt(STATE_WEEK, week);
        instanceState.putString(STATE_SELECTED_DAY, adapter.getSelectedDate());
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
            fillCalendarWeek(month, year);
            adapter.notifyDataSetChanged();
            calendarGridWeek.setAdapter(adapter);
        }
    }

    /**
     * Method: displayMonthYear
     * Sets the current calendar's month and year and updates the CalendarGridAdapter to the new month
     * and year to display the proper dates in each cell of the calendar.
     * @param month the new month
     * @param year the new year
     */
    private void displayMonthYear(int month, int year)
    {
        updateCurrentDay();
        adapter = new CalendarGridAdapter(getApplicationContext(), currentDate);
        setCalendarTitle();
        fillCalendarWeek(month, year);
        adapter.notifyDataSetChanged();
        calendarGridWeek.setAdapter(adapter);
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
     * Method: setCalendarTitle - Weekly
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
        if (view == prevWeekButton)
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

            displayMonthYear(month, year);
        }

        if (view == nextWeekButton)
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

            displayMonthYear(month, year);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getSupportActionBar().setSelectedNavigationItem(1);
    }
}