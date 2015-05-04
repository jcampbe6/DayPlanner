package com.itec4860.dayplanner.database;

/**
 * Class: Task
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 9, 2015.
 *
 * This class
 *
 * Purpose:
 */
public class Task
{
    private long taskID;
    private long projectID;
    private String title;
    private String dueDate;
    private int completed;

    public Task(){}

    public Task(long taskID, long projectID, String title, String dueDate, int completed)
    {
        this.taskID = taskID;
        this.projectID = projectID;
        this.title = title;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public long getTaskID()
    {
        return taskID;
    }

    public void setTaskID(long taskID)
    {
        this.taskID = taskID;
    }

    public long getProjectID()
    {
        return projectID;
    }

    public void setProjectID(long projectID)
    {
        this.projectID = projectID;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(String dueDate)
    {
        this.dueDate = dueDate;
    }

    public int isCompleted()
    {
        return completed;
    }

    public void setCompleted(int completed)
    {
        this.completed = completed;
    }
}
