package com.itec4860.dayplanner;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
/**
 * Created by KWLaptop on 4/27/2015.
 */

public class FetchHolidays extends AsyncTask<URL, Void, Double> {

    private static final String CONV_FMT = "###.##";
    private static final String SINGLE_URL_ONLY = " only accepts a single URL";
    private static final String CONV_LOOKUP = "ConversionLookup";
    private TextView txtView;
    private Gson gson;
    Holidays holidays;
    double cur;

    FetchHolidays(TextView _t) {
        txtView = _t;
        gson = new GsonBuilder().create();
    }

    @Override
    protected Double doInBackground(URL... urls) {

        HttpURLConnection conn = null;
        Scanner scanner = null;
        StringBuilder jsonSB = new StringBuilder();

        try {
            // error if URLs != 1
            if (urls.length != 1)
                throw new IllegalArgumentException(this.getClass().getName() + SINGLE_URL_ONLY);
            // get the connection
            conn = (HttpURLConnection) urls[0].openConnection();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            // connect stream to scanner
            scanner = new Scanner(in);
            // process entire stream
            while (scanner.hasNext()) jsonSB.append(scanner.nextLine());
            Log.v(CONV_LOOKUP, "Response(" + conn.getResponseCode() +
                    "):" + conn.getResponseMessage());
            // TODO handle non-200 errors here

        } catch (IOException e) {
            Log.e(CONV_LOOKUP, e.getStackTrace().toString());
            // return ;
        } finally {
            scanner.close();
            conn.disconnect();
        }
        // convert response body (in builder) to POJO
        String json = jsonSB.toString();
        //ConversionRate rate = gson.fromJson(json, ConversionRate.class);
        holidays = gson.fromJson(json, Holidays.class);
        Log.v(CONV_LOOKUP, json);
        Log.v(CONV_LOOKUP, holidays.toString());
        //return the actual double rate in the POJO
        return holidays.getHol();
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Double result) {
        // set the contents of TextView to the double retrieved in doInBackground
        result = cur * result;
        DecimalFormat format = new DecimalFormat(CONV_FMT);
        String s = format.format(result);
        txtView.setText(s);
    }


}

