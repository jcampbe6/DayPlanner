package com.itec4860.dayplanner;

import android.widget.ImageView;

/**
 * Created by Joshua on 3/10/2015.
 */
public class DateInfoHolder
{
    private String month;
    private String day;
    private String year;
    private String dateColor;
    private ImageView selectedDayImage;

    public DateInfoHolder(String month, String day, String year, String dateColor)
    {
        this.month = month;
        this.day = day;
        this.year = year;
        this.dateColor = dateColor;
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

    public String getDateColor()
    {
        return dateColor;
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
