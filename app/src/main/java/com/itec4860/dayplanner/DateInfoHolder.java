package com.itec4860.dayplanner;

import android.graphics.Color;
import android.widget.ImageView;

/**Class: DateInfoHolder
 * @author Joshua Campbell
 * @version 1.1
 * Course: ITEC 4860 Spring 2015
 * Written: March 10, 2015
 *
 * This class will hold the data/information for a single date displayed on the calendar. The
 * functions will be limited to setters and getters for storing and providing information associated
 * with a date.
 *
 * Purpose: This class represents a single date in the Day Planner application and will serve as
 * the means from which the calendar retrieves data about a date.
 */
public class DateInfoHolder
{
    private String month;
    private String day;
    private String year;
    private int textColor;
    private ImageView selectedDayImage;

    /**
     * Constructor: DateInfoHolder
     * Construct a DateInfoHolder object to hold information about a date such as day, month, year,
     * and eventually events.
     * @param month the month
     * @param day the day
     * @param year the year
     * @param textColor the text color
     */
    public DateInfoHolder(String month, String day, String year, int textColor)
    {
        this.month = month;
        this.day = day;
        this.year = year;
        this.textColor = textColor;
    }

    public String getMonth()
    {
        return month;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public String getDay()
    {
        return day;
    }

    public void setDay(String day)
    {
        this.day = day;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public String getDate()
    {
        return month + " " + day + ", " + year;
    }

    public int getDateTextColor()
    {
        return textColor;
    }

    public ImageView getSelectedDayImage()
    {
        return selectedDayImage;
    }

    public void setSelectedDayImage(ImageView selectedDayImage)
    {
        this.selectedDayImage = selectedDayImage;
    }
}
