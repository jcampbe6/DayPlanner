package com.itec4860.dayplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
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
    private EditText projectNameEditText;
    private static TextView startDateTextView;
    private static TextView dueDateTextView;
    private static TextView dueDateErrorMessage;
    private LinearLayout taskListContainer;
    private DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    // data for saving event
    private String projectType;
    private String projectName;
    private static String projectStartDate;
    private static String projectDueDate;
    private boolean hasTask = false;  // todo: I might not need this
    private ArrayList<ProjectTask> taskList;

    // variables for project date picker
    private static int projStartMonth;
    private static int projStartDay;
    private static int projStartYear;
    private static int projDueMonth;
    private static int projDueDay;
    private static int projDueYear;
    private static final String START_DATE_SOURCE = "start date";
    private static final String DUE_DATE_SOURCE = "due date";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_project_view);

        eventTypeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);
        eventTypeSpinner.setSelection(0);

        projectType = "project";

        taskList = new ArrayList<>();

        projectNameEditText = (EditText) findViewById(R.id.projectNameEditText);
        startDateTextView = (TextView) findViewById(R.id.startDateText);
        dueDateTextView = (TextView) findViewById(R.id.dueDateText);
        dueDateErrorMessage = (TextView) findViewById(R.id.dueDateErrorMsg);

        taskListContainer = (LinearLayout) findViewById(R.id.taskContainer);

        if (getIntent().hasExtra("date"))
        {
            projectStartDate = getIntent().getStringExtra("date");
            startDateTextView.setText(projectStartDate);

            try
            {
                Date date = dateFormatter.parse(projectStartDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                projStartMonth = calendar.get(Calendar.MONTH) + 1;
                projStartDay = calendar.get(Calendar.DAY_OF_MONTH);
                projStartYear = calendar.get(Calendar.YEAR);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        projectDueDate = dueDateTextView.getText().toString();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.eventTypeSpinnerOptions, R.layout.event_type_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.event_type_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(spinnerAdapter);

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0: projectType = "project";
                        break;

                    case 1: projectType = "appointment";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Inner Class: DatePickerFragment
     * Represents the date picker dialog box to set project start date or due date.
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        private int tempMonth;
        private int tempDay;
        private int tempYear;
        private String source;

        public DatePickerFragment(int month, int day, int year, String source)
        {
            tempMonth = month;
            tempDay = day;
            tempYear = year;
            this.source = source;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, tempYear, tempMonth, tempDay);
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            // todo: Do something with the date chosen by the user
            if (source.equalsIgnoreCase(START_DATE_SOURCE))
            {
                projStartMonth = month + 1;
                projStartDay = day;
                projStartYear = year;
                projectStartDate = projStartMonth + "/" + projStartDay + "/" + projStartYear;
                startDateTextView.setText(projectStartDate);
            }

            if (source.equalsIgnoreCase(DUE_DATE_SOURCE))
            {
                // remove error message
                dueDateTextView.setError(null);
                dueDateErrorMessage.setBackgroundDrawable(null);
                dueDateErrorMessage.setText("");

                projDueMonth = month + 1;
                projDueDay = day;
                projDueYear = year;
                projectDueDate = projDueMonth + "/" + projDueDay + "/" + projDueYear;
                dueDateTextView.setText(projectDueDate);
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
            DialogFragment newFragment = new DatePickerFragment(projStartMonth - 1, projStartDay,
                    projStartYear, START_DATE_SOURCE);
            newFragment.show(getFragmentManager(), "datePicker");
        }

        if (view == findViewById(R.id.dueDateSelector))
        {
            DialogFragment newFragment;

            if (projectDueDate.equalsIgnoreCase("Select date"))
            {
                newFragment = new DatePickerFragment(projStartMonth - 1, projStartDay, projStartYear,
                        DUE_DATE_SOURCE);
            }
            else
            {
                newFragment = new DatePickerFragment(projDueMonth - 1, projDueDay, projDueYear,
                        DUE_DATE_SOURCE);
            }

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
        RelativeLayout errorMsgContainer = (RelativeLayout) taskItemRow.findViewById(R.id.taskDueDateErrorMsgContainer);
        TextView dueDateErrorMessageTextView = (TextView) taskItemRow.findViewById(R.id.dueDateErrorMsg);

        // adds task to task list array
        final ProjectTask newTask = new ProjectTask(taskNameEditText, taskDueDateTextView, taskCompletedStatusBox,
                errorMsgContainer, dueDateErrorMessageTextView);
        newTask.setMonthDayYear(projStartMonth, projStartDay, projStartYear);
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
                return new DatePickerDialog(getActivity(), this, newTask.getTaskDueYear(),
                        newTask.getTaskDueMonth() - 1, newTask.getTaskDueDay());
            }

            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                newTask.clearError();

                // Do something with the date chosen by the user
                newTask.setMonthDayYear(month + 1, day, year);
                String taskDueDate = month + 1 + "/" + day + "/" + year;
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

    private boolean validateProjectFields()
    {
        boolean result = true;

        if (!validateProjectName())
        {
            result = false;
        }

        if (!validateProjectDueDate())
        {
            result = false;
        }

        if (taskList.size() > 0)
        {
            for (int i = 0; i < taskList.size(); i++)
            {
                if (!validateTaskName(taskList.get(i)))
                {
                    result = false;
                }

                if (!validateTaskDueDate(taskList.get(i)))
                {
                    result = false;
                }
            }
        }

        return result;
    }

    private boolean validateProjectName()
    {
        boolean result = true;

        if (projectNameEditText.getText().toString().isEmpty())
        {
            projectNameEditText.setError("Must enter a project name");
            result = false;
        }

        return result;
    }

    private boolean validateProjectDueDate()
    {
        // todo: due date validation
        boolean result = true;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (projectDueDate.equalsIgnoreCase("Select date"))
        {
            dueDateTextView.setError("");

            if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN)
            {
                dueDateErrorMessage.setBackground(getResources().getDrawable(R.drawable.error_msg_popup_background));
            }
            else
            {
                dueDateErrorMessage.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_msg_popup_background));
            }

            dueDateErrorMessage.setText("Must select a due date");
            result = false;
        }

        else
        {
            try
            {
                Date startDate = dateFormatter.parse(projectStartDate);
                Date dueDate = dateFormatter.parse(projectDueDate);

                if (dueDate.before(startDate))
                {
                    dueDateTextView.setError("");

                    if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        dueDateErrorMessage.setBackground(getResources().getDrawable(R.drawable.error_msg_popup_background));
                    }
                    else
                    {
                        dueDateErrorMessage.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_msg_popup_background));
                    }

                    dueDateErrorMessage.setText("Due date must be later than start date");
                    result = false;
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    private boolean validateTaskName(ProjectTask task)
    {
        // todo: validate due date > project start date
        boolean result = true;

        if (task.getTaskName().isEmpty())
        {
            result = false;
            task.getTaskNameEditText().setError("Must enter a task name");
        }

        return result;
    }

    private boolean validateTaskDueDate(ProjectTask task)
    {
        boolean result = true;

        try
        {
            Date tempProjStartDate = dateFormatter.parse(projectStartDate);
            Date tempTaskDueDate = dateFormatter.parse(task.getDueDate());

            if (tempTaskDueDate.before(tempProjStartDate))
            {
                task.setError("Due date must be later than start date");

                result = false;
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Method: saveEvent
     * Saves the project data to the database when the save button is clicked.
     * @param view the save button
     */
    public void saveEvent(View view)
    {
        validateProjectFields();
        Toast.makeText(getApplicationContext(), "Save Event Coming Soon", Toast.LENGTH_SHORT).show();
    }

}
