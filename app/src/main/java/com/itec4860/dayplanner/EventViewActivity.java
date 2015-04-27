package com.itec4860.dayplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EventViewActivity extends Activity
{
    // user interface elements
    private Spinner eventTypeSpinner;
    private static TextView startDateText;
    private static TextView dueDateText;

    // data for saving event
    private String type;
    private String title;
    private String startDate;
    private String dueDate;
    private boolean hasTask = false;

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

        type = "project";

        startDateText = (TextView) findViewById(R.id.startDateText);
        dueDateText = (TextView) findViewById(R.id.dueDateText);

        if (getIntent().hasExtra("date"))
        {
            startDate = getIntent().getStringExtra("date");
            startDateText.setText(startDate);

            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            try
            {
                Date date = formatter.parse(startDate);
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
                    type = "project";
                }

                else
                {
                    type = "appointment";
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
                dueDateText.setText((month + 1) + "/" + day + "/" + year);
                isDueDateSelected = false;
            }

            if (isStartDateSelected)
            {
                startDateText.setText((month + 1) + "/" + day + "/" + year);
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
     * Adds a new task for the current project when the new task button is clicked.
     * @param view the new task button
     */
    public void addNewTask(View view)
    {
        Toast.makeText(getApplicationContext(), "Add Task Coming Soon", Toast.LENGTH_SHORT).show();
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
