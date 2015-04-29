package com.itec4860.dayplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EventViewActivity extends Activity
{
    // user interface elements
    private Spinner eventTypeSpinner;
    private static TextView startDateText;
    private static TextView dueDateText;
    private LinearLayout taskListContainer;

    // data for saving event
    private String projectType;
    private String projectTitle;
    private static String projectStartDate;
    private static String projectDueDate;
    private boolean hasTask = false;  // todo: I might not need this
    private ArrayList<ProjectTask> taskList;

    // variables for date picker
    private static int month;
    private static int day;
    private static int year;
    private static boolean isDueDateSelected = false;
    private static boolean isStartDateSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_project_view);

        eventTypeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);
        eventTypeSpinner.setSelection(0);

        projectType = "project";

        taskList = new ArrayList<>();

        startDateText = (TextView) findViewById(R.id.startDateText);
        dueDateText = (TextView) findViewById(R.id.dueDateText);

        taskListContainer = (LinearLayout) findViewById(R.id.taskContainer);

        if (getIntent().hasExtra("date"))
        {
            projectStartDate = getIntent().getStringExtra("date");
            startDateText.setText(projectStartDate);

            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            try
            {
                Date date = formatter.parse(projectStartDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                year = calendar.get(Calendar.YEAR);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.eventTypeSpinnerOptions, R.layout.event_type_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.event_type_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(spinnerAdapter);

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 0)
                {
                    projectType = "project";
                }

                else
                {
                    projectType = "appointment";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    /**
     * Inner Class: DatePickerFragment
     * Represents the date picker dialog box to set project start date or due date.
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            // Do something with the date chosen by the user
            if (isDueDateSelected)
            {
                projectDueDate = (month + 1) + "/" + day + "/" + year;
                dueDateText.setText(projectDueDate);
                isDueDateSelected = false;
            }

            if (isStartDateSelected)
            {
                projectStartDate = (month + 1) + "/" + day + "/" + year;
                startDateText.setText(projectStartDate);
                isStartDateSelected = false;
            }
        }
    }

    /**
     * Method:  showDatePickerDialog
     * Displays the date picker dialog when the start date or end date buttons are clicked.
     * @param view the button that was clicked
     */
    public void showDatePickerDialog(View view)
    {
        if (view == findViewById(R.id.startDateSelector))
        {
            isStartDateSelected = true;
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }

        if (view == findViewById(R.id.dueDateSelector))
        {
            isDueDateSelected = true;
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }
    }

    /**
     * Method: addNewTask
     * Dynamically adds a new task for the current project when the new task button is clicked.
     * @param view the new task button
     */
    public void addNewTask(View view)
    {
        // inflates/displays a task
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View taskItemRow = layoutInflater.inflate(R.layout.task_row_layout, null);

        // task fields
        EditText taskNameEditText = (EditText) taskItemRow.findViewById(R.id.taskName);
        RelativeLayout taskDueDateButton = (RelativeLayout) taskItemRow.findViewById(R.id.taskDueDateButton);
        final TextView taskDueDateTextView = (TextView) taskItemRow.findViewById(R.id.taskDueDate);
        taskDueDateTextView.setText(projectStartDate);
        CheckBox taskCompletedStatusBox = (CheckBox) taskItemRow.findViewById(R.id.taskCompletedStatus);

        // adds task to task list array
        final ProjectTask newTask = new ProjectTask(taskNameEditText, taskDueDateTextView, taskCompletedStatusBox);
        taskList.add(newTask);

        /**
         * Inner Class: TaskDatePickerFragment
         * Represents the date picker dialog box to set task due date.
         */
        class TaskDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
        {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState)
            {
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }

            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                // Do something with the date chosen by the user
                String taskDueDate = (month + 1) + "/" + day + "/" + year;
                taskDueDateTextView.setText(taskDueDate);
            }
        }

        taskDueDateButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DialogFragment newFragment = new TaskDatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        ImageView removeTaskButton = (ImageView) taskItemRow.findViewById(R.id.removeTaskButton);
        removeTaskButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                int index = taskList.indexOf(newTask);
//                Toast.makeText(getApplicationContext(), "" + taskList.get(index).isTaskCompleted(), Toast.LENGTH_SHORT).show();

                // removes task from task list array, then from the user interface/layout
                taskList.remove(newTask);
                ((LinearLayout) taskItemRow.getParent()).removeView(taskItemRow);
            }
        });

        // adds task to user interface/layout
        taskListContainer.addView(taskItemRow);
    }

    /**
     * Method: saveEvent
     * Saves the project data to the database when the save button is clicked.
     * @param view the save button
     */
    public void saveEvent(View view)
    {
        Toast.makeText(getApplicationContext(), "Save Event Coming Soon", Toast.LENGTH_SHORT).show();
    }

}
