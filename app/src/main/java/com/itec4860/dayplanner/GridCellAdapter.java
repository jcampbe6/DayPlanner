package com.itec4860.dayplanner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**Class: GridCellAdapter
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: March 6, 2015
 *
 * This class will...
 *
 * Purpose: This class ...
 */

public class GridCellAdapter extends BaseAdapter implements View.OnClickListener
{
    private int currentDayOfMonth;
    private int selectedDay;

    private final Context CONTEXT;
    private final List<String> GRID_CELL_INFO_LIST = new ArrayList<String>();
    private final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private final int[] DAYS_OF_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String CURRENT_DATE_COLOR = "white";
    private final String CURRENT_MONTH_DATE_COLOR = "black";
    private final String NEXT_OR_PREV_MONTH_DATE_COLOR = "lightgrey";

    /**
     * Constructor: GridCellAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     * @param day the current day
     * @param month the current month
     * @param year the current year
     */
    public GridCellAdapter(Context context, int day, int month, int year)
    {
        super();

        this.CONTEXT = context;
        currentDayOfMonth = day;

        fillCalendar(month, year);
    }

    /**
     * Constructor: GridCellAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     * @param day the current day
     * @param month the current month
     * @param year the current year
     * @param selectedDay the day to display as selected
     */
    public GridCellAdapter(Context context, int day, int month, int year, int selectedDay)
    {
        super();

        this.CONTEXT = context;
        currentDayOfMonth = day;
        this.selectedDay = selectedDay;

        fillCalendar(month, year);
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
        return DAYS_OF_MONTH[monthByNum - 1];
    }

    public int getSelectedDay()
    {
        return selectedDay;
    }

    private void setSelectedDay(int day)
    {
        selectedDay = day;
    }



    //TODO
    @Override
    public int getCount()
    {
        return GRID_CELL_INFO_LIST.size();
    }

    //TODO
    @Override
    public String getItem(int position)
    {
        return GRID_CELL_INFO_LIST.get(position);
    }

    /**
     * Class: ViewHolder
     * To hold each calendar day's related info.
     */
    static class DateInfoHolder
    {
        protected String month;
        protected String day;
        protected String year;
        protected String date;
        protected ImageButton selectedDayIndicator;
    }

    /**
     * Method: fillCalendar
     * Fills the calendar grid with cells representing each day in a month in a given year.
     * @param currentMonthNum the current or specified month
     * @param currentYear the current of specified year
     */
    private void fillCalendar(int currentMonthNum, int currentYear)
    {
        int numOfLeadingDays;
        int daysInPrevMonth;
        int prevMonth;
        int yearOfPrevMonth;
        int nextMonth;
        int yearOfNextMonth;
        int daysInCurrentMonth = getMonthTotalDays(currentMonthNum);

        // adjusted currentMonthNum by -1 for GregorianCalendar compatibility
        GregorianCalendar calendar = new GregorianCalendar(currentYear, currentMonthNum - 1, 1);

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

        if (calendar.isLeapYear(currentYear) && currentMonthNum == 2)
        {
            daysInCurrentMonth++;
        }

        numOfLeadingDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //calculate the 'day of the month' for each day of the previous month that leads into the
        //first week of the current month and add info for those days to the cell info list
        for (int i = 1; i <= numOfLeadingDays; i++)
        {
            int leadingDayOfMonth = daysInPrevMonth - numOfLeadingDays + i;

            GRID_CELL_INFO_LIST.add(NEXT_OR_PREV_MONTH_DATE_COLOR + "-" + String.valueOf(leadingDayOfMonth)
                    + "-" + getMonthName(prevMonth) + "-" + yearOfPrevMonth);
        }

        //add info for each day of the current month to the cell info list
        for (int i = 0; i < daysInCurrentMonth; i++)
        {
            if (i == currentDayOfMonth)
            {
                GRID_CELL_INFO_LIST.add(CURRENT_DATE_COLOR + "-" + String.valueOf(i)
                        + "-" + getMonthName(currentMonthNum) + "-" + currentYear);
            }

            else
            {
                GRID_CELL_INFO_LIST.add(CURRENT_MONTH_DATE_COLOR + "-" + String.valueOf(i + 1)
                        + "-" + getMonthName(currentMonthNum) + "-" + currentYear);
            }
        }

        //TODO: FIND OUT WHY THIS WORKS!!! should not complete loop, should be 1 short
        //add info for each day of the next month that trails out of the last week of the current
        //month to the cell info list
        for (int i = 0; i < GRID_CELL_INFO_LIST.size() % 7; i++)
        {
            GRID_CELL_INFO_LIST.add(NEXT_OR_PREV_MONTH_DATE_COLOR + "-" + String.valueOf(i + 1)
                    + "-" + getMonthName(nextMonth) + "-" + yearOfNextMonth);
        }
    }

    /**
     * Method: getItemId
     * Returns the id of the cell at a specified position. Here, the id is the same as the position.
     * @param position the specified cell position
     * @return position the id of the specified cell
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Method: getView
     * Returns the view of a single cell in the calendar grid.
     * @param position the position of the cell
     * @param convertView the cell view used to inflate the layout
     * @param parent the cell's parent view (the grid view)
     * @return cell the view of the cell
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View cell = convertView;

        if (cell == null)
        {
            LayoutInflater inflater = (LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = inflater.inflate(R.layout.grid_cell, parent, false);
        }

        Button gridCell = (Button) cell.findViewById(R.id.calendarDayGridCell);
        gridCell.setOnClickListener(this);

        String[] gridCellInfo = GRID_CELL_INFO_LIST.get(position).split("-");
        String gridCellDateColor = gridCellInfo[0];

        final DateInfoHolder dateInfoHolder = new DateInfoHolder();
        dateInfoHolder.day = gridCellInfo[1];
        dateInfoHolder.month = gridCellInfo[2];
        dateInfoHolder.year = gridCellInfo[3];
        dateInfoHolder.selectedDayIndicator = (ImageButton) cell.findViewById(R.id.selectedDayIndicator);
        dateInfoHolder.date = dateInfoHolder.month + " " + dateInfoHolder.day + ", " + dateInfoHolder.year;

        gridCell.setText(dateInfoHolder.day);
        gridCell.setTag(dateInfoHolder);

        if (gridCellDateColor.equals(NEXT_OR_PREV_MONTH_DATE_COLOR))
        {
            gridCell.setTextColor(Color.parseColor(NEXT_OR_PREV_MONTH_DATE_COLOR));
        }

        if (gridCellDateColor.equals(CURRENT_MONTH_DATE_COLOR))
        {
            gridCell.setTextColor(Color.parseColor(CURRENT_MONTH_DATE_COLOR));
        }

        if (gridCellDateColor.equals(CURRENT_DATE_COLOR))
        {
            gridCell.setTextAppearance(CONTEXT, android.R.style.TextAppearance_Large);
            gridCell.setTextColor(Color.parseColor(CURRENT_DATE_COLOR));
            dateInfoHolder.selectedDayIndicator.setImageResource(R.drawable.circle_solid);
        }

        if (Integer.parseInt(dateInfoHolder.day) == selectedDay)
        {
            dateInfoHolder.selectedDayIndicator.setFocusable(true);
            dateInfoHolder.selectedDayIndicator.setFocusableInTouchMode(true);
            dateInfoHolder.selectedDayIndicator.requestFocus();
        }

        return cell;
    }

    /**
     * Method: onClick
     * Will eventually perform calendar specific functions, such as create a new calendar event.
     * @param view the cell that was clicked
     */
    @Override
    public void onClick(View view)
    {
        DateInfoHolder dateInfoHolder = (DateInfoHolder) view.getTag();
        dateInfoHolder.selectedDayIndicator.setFocusable(true);
        dateInfoHolder.selectedDayIndicator.setFocusableInTouchMode(true);
        dateInfoHolder.selectedDayIndicator.requestFocus();

        Toast.makeText(CONTEXT, dateInfoHolder.date, Toast.LENGTH_SHORT).show();

        setSelectedDay(Integer.parseInt(dateInfoHolder.day));
    }
}
