package com.itec4860.dayplanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
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

/**Class: RegisterActivity
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 2, 2015
 *
 * This class will handle user registration for the Day Planner application.
 *
 * Purpose: This class provides a user interface to allow an individual to
 * register to use the Day Planner application.
 */
public class RegisterActivity extends Activity
{
    private TextView regErrorMsgTextView;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressDialog regProgressDialog;
    private Context context;
    private final String TAG = "register";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();
        regErrorMsgTextView = (TextView) findViewById(R.id.regErrorMsgTextView);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        regProgressDialog = new ProgressDialog(this);
        regProgressDialog.setCancelable(false);
        regProgressDialog.setMessage("Registering...");

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                regErrorMsgTextView.setText("");

                registerUser(username, email, password);
            }
        });

        Button loginLinkButton = (Button) findViewById(R.id.loginLinkButton);
        loginLinkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent loginIntent = new Intent(context, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    /**
     * Method: registerUser
     * Attempts to register a user by sending a POST request to the Day Planner server with a username,
     * an email, and a password, and listens for various responses: response from the API response
     * listener, which is a response from the server or the API on the server, and a response from a
     * request error listener. A response from the server will be either a failure: "error" = 'true'
     * with an "errorMsg" about the error; or a success: "error" = 'false' along with the user info
     * of the user that was successfully registered and which must subsequently be stored on the
     * device's local SQLite database. A response from the request error listener basically states
     * that the request was not successfully transmitted due to some connectivity error or an error
     * on the server, ie. request timed out, internet connection issue, server is down, etc.
     * @param username the username to register
     * @param email the email to register
     * @param password the password to register
     */
    private void registerUser(final String username, final String email, final String password)
    {
        if (!hasInternetConnection())
        {
            regErrorMsgTextView.setText("No internet connectivity");
        }

        else if (validateRegInputFields(username, email, password))
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            displayRegProgressDialog();

            Response.Listener<String> apiResponseListener = new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    dismissRegProgressDialog();
                    try
                    {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean error = jsonResponse.getBoolean("error");
                        if (error)
                        {
                            String errorMsg = jsonResponse.getString("errorMsg");
                            regErrorMsgTextView.setText(errorMsg);
                        }

                        else
                        {
                            // TODO: store user info in SQLite database -- must create database first

                            // store user info in preferences
                            String userID = jsonResponse.getString("userID");
                            String username = jsonResponse.getString("username");

                            // sets the user's registration status within the application to 'true'
                            SharedPreferences dayPlannerSettings = getSharedPreferences("dayPlannerSettings", MODE_PRIVATE);
                            SharedPreferences.Editor settingsEditor = dayPlannerSettings.edit();
                            settingsEditor.putBoolean("isUserRegistered", true);
                            settingsEditor.putString("username", username);
                            settingsEditor.putString("userID", userID);
                            settingsEditor.apply();

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
                        regErrorMsgTextView.setText(errorMsg);
                    }
                    else if (error instanceof NoConnectionError)
                    {
                        errorMsg = "Network is unreachable, please check your internet connection";
                        regErrorMsgTextView.setText(errorMsg);
                    }
                    else if (error instanceof ServerError)
                    {
                        errorMsg = "Server error";
                        regErrorMsgTextView.setText(errorMsg);
                    }

                    dismissRegProgressDialog();

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
                    params.put("username", username);
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };

            queue.add(strRequest);
        }
    }

    /**
     * Method validateRegInputFields
     * Runs the appropriate validation for each user registration input field.
     * @param username the username to check
     * @param email the email to check
     * @param password the password to check
     * @return a boolean verification result
     */
    private boolean validateRegInputFields(String username, String email, String password)
    {
        boolean result = true;

        if (!validateUsername(username))
        {
            result = false;
        }

        if (!validateEmail(email))
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
     * Method: validateUsername
     * Checks to make sure that a username has been entered.
     * @param username the username to verify
     * @return the boolean validation result
     */
    private boolean validateUsername(String username)
    {
        if (username.isEmpty())
        {
            usernameEditText.setError("Must enter a username");
            return false;
        }

        return true;
    }

    /**
     * Method: validateEmail
     * Checks to make sure that an email has been entered and that it meets the proper email
     * address format.
     * @param email the email to verify
     * @return the boolean validation result
     */
    private boolean validateEmail(String email)
    {
        if (email.isEmpty())
        {
            emailEditText.setError("Must enter an email address");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Invalid Email Address");
            return false;
        }

        return true;
    }

    /**
     * Method: validatePassword
     * Checks to make sure that a password has been entered and that it meets the criteria for a
     * strong password.
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

        String passwordStrengthRegex = "(?=^.{8,}$)(?=.*[A-Z])(?=.*[!@#$%]+).*$";

        if (!password.matches(passwordStrengthRegex))
        {
            passwordEditText.setError("Password must:\n" +
                    "- be at least 8 characters long\n" +
                    "- contain at least 1 uppercase letter\n" +
                    "- contain at least 1 special character (!@#$%)");

            return false;
        }

        return true;
    }

    /**
     * Method: displayRegProgressDialog
     * Displays a progress dialog during registration.
     */
    private void displayRegProgressDialog()
    {
        if (!regProgressDialog.isShowing())
        {
            regProgressDialog.show();
        }
    }

    /**
     * Method: dismissRegProgressDialog
     * Dismisses the registration progress dialog.
     */
    private void dismissRegProgressDialog()
    {
        if (regProgressDialog.isShowing())
        {
            regProgressDialog.dismiss();
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
