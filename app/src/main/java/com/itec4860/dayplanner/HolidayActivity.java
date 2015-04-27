package com.itec4860.dayplanner;

import java.net.MalformedURLException;
import java.net.URL;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
/**
 * Created by KWLaptop on 4/27/2015.
 */


public class HolidayActivity extends ActionBarActivity {

    private static String CONV_URL = "http://http://kayaposoft.com/enrico/json/";

    String curYear = "2014";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holidays);
            //@Overrride

//                Spinner SelCurrencyF = (Spinner)findViewById(R.id.SpinCurrFrom);
//                String SelCurrFromStr = SelCurrencyF.getSelectedItem().toString();
//                Spinner SelCurrencyT = (Spinner)findViewById(R.id.spinCurrTo);
//                String SelCurrToStr = SelCurrencyT.getSelectedItem().toString();
                // ?action=getPublicHolidaysForYear&year=2013&country=est&region=
                String params = "?action=getPublicHolidaysForYear&year=" + curYear + "&country=est&region=";

//                EditText curFromT = (EditText) findViewById(R.id.TextCurrFrom);
//                double curFrom = Double.parseDouble(curFromT.getText().toString());
//                TextView curToText=(TextView)findViewById(R.id.TextCurrTo);


                FetchHolidays task = new FetchHolidays(curYear);
                try {
                    task.execute(new URL(CONV_URL + params));
                } catch (MalformedURLException e) {
                    // TODO add Toast or other notification
                    e.printStackTrace();
                }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
