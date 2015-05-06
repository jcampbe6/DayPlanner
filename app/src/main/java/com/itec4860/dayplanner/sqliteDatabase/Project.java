package com.itec4860.dayplanner.sqliteDatabase;

/**
 * Class: Project
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: May 04, 2015
 * 
 * This class...
 * 
 * Purpose: 
 */
public class Project extends Event
{
    private String startDate;
    private String endDate;
    private int hasTask;

    public Project(){}

    public Project(long eventID, String title, String startDate, String endDate, int hasTask)
    {
        super(eventID, title, "project");

        this.startDate = startDate;
        this.endDate = endDate;
        this.hasTask = hasTask;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public int hasTask()
    {
        return hasTask;
    }

    public void setHasTask(int hasTask)
    {
        this.hasTask = hasTask;
    }
}
