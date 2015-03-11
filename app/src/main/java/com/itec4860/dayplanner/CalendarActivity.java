package com.itec4860.dayplanner;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
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
    private int oldDay;
    private int currentDay;
    private int month;
    private int year;
    private boolean navItemSelectedOnCreate;

    private final DateFormat DATE_FORMATTER = new DateFormat();
    private static final String DATE_FORMAT = "MMMM yyyy";
    private final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private final int[] DAYS_OF_THE_MONTHS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String TODAY_DATE_COLOR = "white";
    private final String CURRENT_MONTH_DATE_COLOR = "black";
    private final String NEXT_AND_PREV_MONTH_DATE_COLOR = "lightgrey";

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
                R.array.calendar_view_options_list, R.layout.action_bar_dropdown_item);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);

        prevMonthButton = (ImageButton) this.findViewById(R.id.prevMonth);
        prevMonthButton.setOnClickListener(this);

        calendar = Calendar.getInstance(Locale.getDefault());
        setCurrentDateInfo();

        currentMonthTitle = (TextView) this.findViewById(R.id.currentMonthTitle);
        currentMonthTitle.setText(DATE_FORMATTER.format(DATE_FORMAT, calendar.getTime()));

        nextMonthButton = (ImageButton) this.findViewById(R.id.nextMonth);
        nextMonthButton.setOnClickListener(this);

        calendarGrid = (GridView) this.findViewById(R.id.calendarGrid);
        registerForContextMenu(calendarGrid);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SELECTED_DAY))
        {
            adapter = new GridCellAdapter(getApplicationContext(), currentDay,
                    savedInstanceState.getString(STATE_SELECTED_DAY));
        }

        else
        {
            adapter = new GridCellAdapter(getApplicationContext(), currentDay);
        }

        fillCalendar(currentDay, month, year);

        adapter.notifyDataSetChanged();
        calendarGrid.setAdapter(adapter);
        calendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DateInfoHolder dateInfoHolder = (DateInfoHolder) view.getTag();

                Toast.makeText(getApplicationContext(), dateInfoHolder.getDate(), Toast.LENGTH_SHORT).show();

                adapter.setSelectedDate(dateInfoHolder.getDate());
                adapter.notifyDataSetChanged();
            }
        });


    }

    private void setCurrentDateInfo()
    {
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void updateCurrentDay()
    {
        oldDay = currentDay;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Method: fillCalendar
     * Fills the calendar grid with cells representing each day in a month in a given year.
     * @param currentMonthNum the current or specified month
     * @param currentYear the current of specified year
     */
    private void fillCalendar(int currentDayOfMonth, int currentMonthNum, int currentYear)
    {
        int daysInPrevMonth;
        int prevMonth;
        int yearOfPrevMonth;
        int nextMonth;
        int yearOfNextMonth;
        int daysInCurrentMonth = getMonthTotalDays(currentMonthNum);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, view, menuInfo);


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);

    }

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
        instanceState.putString(STATE_SELECTED_DAY, adapter.getSelectedDate());
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

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        updateCurrentDay();

        /*if (currentDay != oldDay)
        {
            adapter = new GridCellAdapter(getApplicationContext(), currentDay);
            fillCalendar(currentDay, month, year);
            adapter.notifyDataSetChanged();
            calendarGrid.setAdapter(adapter);
        }*/
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
        updateCurrentDay();
        adapter = new GridCellAdapter(getApplicationContext(), currentDay);
        calendar.set(year, month - 1, currentDay);
        currentMonthTitle.setText(DATE_FORMATTER.format(DATE_FORMAT, calendar.getTime()));
        fillCalendar(currentDay, month, year);
        adapter.notifyDataSetChanged();
        calendarGrid.setAdapter(adapter);
    }

    /**
     * Method: getMonthName
     * Returns the name of a month that is specified by the month number.
     * @param monthByNum the number of the desired month
     * @return the name of the month
     */
    private String getMonthName(int monthByNum)
    {
        return MONTHS[monthByNum - 1];
    }

    /**
     * Method: getMonthTotalDays
     * Returns the total number of days in a month specified by the month number.
     * @param monthByNum the number of the desired month
     * @return the number of days
     */
    private int getMonthTotalDays(int monthByNum)
    {
        return DAYS_OF_THE_MONTHS[monthByNum - 1];
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