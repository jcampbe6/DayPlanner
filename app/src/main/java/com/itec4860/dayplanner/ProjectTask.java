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
    private EditText taskName;
    private TextView dueDate;
    private CheckBox completedStatus;

    public ProjectTask(EditText taskName, TextView dueDate, CheckBox completedStatus)
    {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.completedStatus = completedStatus;
    }

    public String getTaskName()
    {
        return taskName.getText().toString();
    }

    public String getDueDate()
    {
        return dueDate.getText().toString();
    }

    public boolean isTaskCompleted()
    {
        return completedStatus.isChecked();
    }
}
