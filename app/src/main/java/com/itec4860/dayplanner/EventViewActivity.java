package com.itec4860.dayplanner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class EventViewActivity extends ActionBarActivity
{
    private Spinner eventTypeSpinner;
    private TextView startDateText;
    private TextView dueDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_project_view);

        eventTypeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);
        startDateText = (TextView) findViewById(R.id.startDateText);
        dueDateText = (TextView) findViewById(R.id.dueDateText);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.eventTypeSpinnerOptions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(spinnerAdapter);
        eventTypeSpinner.setSelection(0);
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    public void showDateSelectorDialog(View view)
    {
        if (view == findViewById(R.id.startDateSelector))
        {
            startDateText.setText("A start date");
        }

        if (view == findViewById(R.id.dueDateSelector))
        {
            dueDateText.setText("A due date");
        }
    }

    public void addNewTask(View view)
    {
        Toast.makeText(getApplicationContext(), "Add Task Coming Soon", Toast.LENGTH_SHORT).show();
    }

    public void saveEvent(View view)
    {
        Toast.makeText(getApplicationContext(), "Save Event Coming Soon", Toast.LENGTH_SHORT).show();
    }

}
