package com.itec4860.dayplanner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class EventActivity extends ActionBarActivity {

    private Spinner eventTypeSpinner;
    private Button saveButton;
    private Button addUserButton;
    private EditText addedUserField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        addUserButton = (Button) findViewById(R.id.addUserButton);
//        addedUserField = (EditText) findViewById(R.id.addedUserField);

        addItemsOnSpinner();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

//        When pressed the addUser button creates an new editable text field representing a user.
//        This is infinitely repeatable.
                addUserButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //when addUser is clicked show the new textField
                EditText newUserTextField = new EditText(v.getContext());  // create TextField
                newUserTextField.setText("");      //  Set the default text of the Field
                // set params and add button to layout

            }
        });

        //the save button saves the data inputted
        // currently just prints text instead.
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    }

}
