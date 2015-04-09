package com.itec4860.dayplanner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Timothy on 4/8/2015.
 */
public class EventActivity extends Activity{

    private Spinner eventTypeSpinner;
    private Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_view);

        addItemsOnSpinner();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner() {

        eventTypeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Month");
        list.add("Week");
        list.add("Day");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
        eventTypeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);
        eventTypeSpinner.setOnItemSelectedListener(new CustomProjectTypeListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        eventTypeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);

//the save button doesn't do anything important right now
        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(EventActivity.this,
                        "OnClickListener : " + "\nEvent Type: " +
                                String.valueOf(eventTypeSpinner.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

}
