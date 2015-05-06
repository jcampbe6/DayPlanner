package com.itec4860.dayplanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**Class: LoginActivity
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 6, 2015
 *
 * This class will handle user login for the Day Planner application.
 *
 * Purpose: This class provides a user interface to allow a user to
 * login to use the Day Planner application.
 */
public class LoginActivity extends Activity
{
    private TextView loginErrorMsgTextView;
    private EditText usernameOrEmailEditText;
    private EditText passwordEditText;
    private ProgressDialog loginProgressDialog;
    private Context context;
    private final String TAG = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        loginErrorMsgTextView = (TextView) findViewById(R.id.loginErrorMsgTextView);
        usernameOrEmailEditText = (EditText) findViewById(R.id.usernameOrEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        loginProgressDialog = new ProgressDialog(this);
        loginProgressDialog.setCancelable(false);
        loginProgressDialog.setMessage("Logging in...");

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String usernameOrEmail = usernameOrEmailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginErrorMsgTextView.setText("");

                login(usernameOrEmail, password);
            }
        });

        Button registerLinkButton = (Button) findViewById(R.id.registerLinkButton);
        registerLinkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent registrationIntent = new Intent(context, RegisterActivity.class);
                startActivity(registrationIntent);
                finish();
            }
        });
    }

    /**
     * Method: login
     * Attempts to log a user in by sending a POST request to the Day Planner server with a username
     * or an email, and a password, and listens for various responses: response from the API response
     * listener, which is a response from the server or the API on the server, and a response from a
     * request error listener. A response from the server will be either a failure: "error" = 'true'
     * with an "errorMsg" about the error; or a success: "error" = 'false' along with the user info
     * of the user that was successfully logged in and which must subsequently be stored on the
     * device's local SQLite database. A response from the request error listener basically states
     * that the request was not successfully transmitted due to some connectivity error or an error
     * on the server, ie. request timed out, internet connection issue, server is down, etc.
     * @param usernameOrEmail the username/email to use to login
     * @param password the password to use to login
     */
    private void login(final String usernameOrEmail, final String password)
    {
        if (!hasInternetConnection())
        {
            loginErrorMsgTextView.setText("No internet connectivity");
        }

        if (validateLoginInputFields(usernameOrEmail, password))
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            displayLoginProgressDialog();

            Response.Listener<String> apiResponseListener = new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    dismissLoginProgressDialog();
                    try
                    {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean error = jsonResponse.getBoolean("error");
                        if (error)
                        {
                            String errorMsg = jsonResponse.getString("errorMsg");
                            loginErrorMsgTextView.setText(errorMsg);
                        }

                        else
                        {
                            // store user info and registration status in preferences
                            String userID = jsonResponse.getString("userID");
                            String username = jsonResponse.getString("username");
                            SharedPreferences dayPlannerSettings = getSharedPreferences("dayPlannerSettings", MODE_PRIVATE);
                            SharedPreferences.Editor settingsEditor = dayPlannerSettings.edit();
                            settingsEditor.putBoolean("isUserRegistered", true);
                            settingsEditor.putString("username", username);
                            settingsEditor.putString("userID", userID);
                            settingsEditor.apply();

                            // TODO: retrieve all user events from online database and store in SQLite database

                            Intent viewCalendarIntent = new Intent(context, CalendarActivity.class);
                            startActivity(viewCalendarIntent);
                            finish();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    // TODO: for api response testing
//                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    String errorMsg;
                    if (error instanceof TimeoutError)
                    {
                        errorMsg = "Request timed out";
                        loginErrorMsgTextView.setText(errorMsg);
                    }
                    else if (error instanceof NoConnectionError)
                    {
                        errorMsg = "Network is unreachable, please check your internet connection";
                        loginErrorMsgTextView.setText(errorMsg);
                    }
                    else if (error instanceof ServerError)
                    {
                        errorMsg = "Server error";
                        loginErrorMsgTextView.setText(errorMsg);
                    }

                    dismissLoginProgressDialog();

                    // TODO: for error response testing
//                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            };

            StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.day_planner_api_url),
                    apiResponseListener, errorListener)
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("tag", TAG);
                    params.put("usernameOrEmail", usernameOrEmail);
                    params.put("password", password);
                    return params;
                }
            };

            queue.add(strRequest);
        }
    }

    /**
     * Method validateLoginInputFields
     * Runs the appropriate validation for each user login input field.
     * @param username the username to check
     * @param password the password to check
     * @return a boolean verification result
     */
    private boolean validateLoginInputFields(String username, String password)
    {
        boolean result = true;

        if (!validateUsernameOrEmail(username))
        {
            result = false;
        }

        if (!validatePassword(password))
        {
            result = false;
        }

        return result;
    }

    /**
     * Method: validateUsernameOrEmail
     * Checks to make sure that a username/email has been entered.
     * @param username the username to verify
     * @return the boolean validation result
     */
    private boolean validateUsernameOrEmail(String username)
    {
        if (username.isEmpty())
        {
            usernameOrEmailEditText.setError("Must enter a username or email");
            return false;
        }

        return true;
    }

    /**
     * Method: validatePassword
     * Checks to make sure that a password has been entered.
     * @param password the password to verify
     * @return the boolean validation result
     */
    private boolean validatePassword(String password)
    {
        if (password.isEmpty())
        {
            passwordEditText.setError("Must enter a password");
            return false;
        }

        return true;
    }

    /**
     * Method: displayLoginProgressDialog
     * Displays a progress dialog during login.
     */
    private void displayLoginProgressDialog()
    {
        if (!loginProgressDialog.isShowing())
        {
            loginProgressDialog.show();
        }
    }

    /**
     * Method: dismissLoginProgressDialog
     * Dismisses the login progress dialog.
     */
    private void dismissLoginProgressDialog()
    {
        if (loginProgressDialog.isShowing())
        {
            loginProgressDialog.dismiss();
        }
    }

    /**
     * Method: hasInternetConnection
     * Checks to see if the mobile device has an active internet connection.
     * @return isConnected the boolean result of the verification
     */
    private boolean hasInternetConnection()
    {
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
