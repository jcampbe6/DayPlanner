package com.itec4860.dayplanner;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Class: ProjectTask
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 28, 2015
 * 
 * This class...
 * 
 * Purpose: 
 */
public class ProjectTask 
{
    private EditText taskNameEditText;
    private TextView dueDateTextView;
    private CheckBox completedStatusCheckBox;
    private int taskDueMonth;
    private int taskDueDay;
    private int taskDueYear;

    public ProjectTask(EditText taskName, TextView dueDate, CheckBox completedStatus)
    {
        taskNameEditText = taskName;
        dueDateTextView = dueDate;
        completedStatusCheckBox = completedStatus;
    }

    public String getTaskName()
    {
        return taskNameEditText.getText().toString();
    }

    public String getDueDate()
    {
        return dueDateTextView.getText().toString();
    }

    public boolean isTaskCompleted()
    {
        return completedStatusCheckBox.isChecked();
    }

    public void setMonthDayYear(int month, int day, int year)
    {
        taskDueMonth = month;
        taskDueDay = day;
        taskDueYear = year;
    }

    public int getTaskDueMonth()
    {
        return taskDueMonth;
    }

    public int getTaskDueDay()
    {
        return taskDueDay;
    }

    public int getTaskDueYear()
    {
        return taskDueYear;
    }
}
