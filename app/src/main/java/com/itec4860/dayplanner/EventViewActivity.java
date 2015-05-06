package com.itec4860.dayplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itec4860.dayplanner.sqliteDatabase.Project;
import com.itec4860.dayplanner.sqliteDatabase.ProjectDAO;
import com.itec4860.dayplanner.sqliteDatabase.Task;
import com.itec4860.dayplanner.sqliteDatabase.TaskDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**Class: EventViewActivity
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 27, 2015
 *
 * This class will handle the creation of new events for a specified date.
 *
 * Purpose: This class provides a user interface to allow a user to
 * create a new event.
 */
public class EventViewActivity extends Activity
{
    // user interface elements
    private EditText projectNameEditText;
    private static TextView startDateTextView;
    private static TextView dueDateTextView;
    private static TextView dueDateErrorMessage;
    private LinearLayout taskListContainer;
    private ProgressDialog saveEventProgressDialog;
    private final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
    private final String PROJECT_NAME_ERROR_MSG = "Must enter a project name";
    private final String DUE_DATE_ERROR_MSG_1 = "Must select a due date";
    private final String DUE_DATE_ERROR_MSG_2 = "Due date cannot be earlier than start date";
    private final String TASK_NAME_ERROR_MSG = "Must enter a task name";

    // data for saving event
    private final String TAG = "save event";
    private String userID;
    private String eventType;
    private String projectName;
    private static String projectStartDate;
    private static String projectDueDate;
    private ArrayList<ProjectTaskInfoHolder> taskList;

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

        Spinner eventTypeSpinner = (Spinner) findViewById(R.id.eventTypeSpinner);
        eventTypeSpinner.setSelection(0);

        SharedPreferences dayPlannerSettings = getSharedPreferences("dayPlannerSettings", MODE_PRIVATE);
        userID = dayPlannerSettings.getString("userID", "user id not set");

        projectNameEditText = (EditText) findViewById(R.id.projectNameEditText);
        startDateTextView = (TextView) findViewById(R.id.startDateText);
        dueDateTextView = (TextView) findViewById(R.id.dueDateText);
        dueDateErrorMessage = (TextView) findViewById(R.id.projectDueDateErrorMsg);
        taskListContainer = (LinearLayout) findViewById(R.id.taskContainer);
        taskList = new ArrayList<>();

        saveEventProgressDialog = new ProgressDialog(this);
        saveEventProgressDialog.setCancelable(false);
        saveEventProgressDialog.setMessage("Saving...");

        if (getIntent().hasExtra("date"))
        {
            projectStartDate = getIntent().getStringExtra("date");
            startDateTextView.setText(projectStartDate);

            try
            {
                Date date = DATE_FORMATTER.parse(projectStartDate);
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
                    case 0: eventType = "project";
                        break;

                    case 1: eventType = "appointment";
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
    private static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
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
            // do something with the date chosen by the user
            if (source.equalsIgnoreCase(START_DATE_SOURCE))
            {
                if (!projectDueDate.equalsIgnoreCase("Select date"))
                {
                    dueDateTextView.setError(null);
                    dueDateErrorMessage.setBackgroundDrawable(null);
                    dueDateErrorMessage.setText("");
                }

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

        // initialize task fields
        EditText taskNameEditText = (EditText) taskItemRow.findViewById(R.id.taskName);
        RelativeLayout taskDueDateButton = (RelativeLayout) taskItemRow.findViewById(R.id.taskDueDateButton);
        final TextView taskDueDateTextView = (TextView) taskItemRow.findViewById(R.id.taskDueDate);
        taskDueDateTextView.setText(projectStartDate);
        CheckBox taskCompletedStatusBox = (CheckBox) taskItemRow.findViewById(R.id.taskCompletedStatus);
        RelativeLayout errorMsgContainer = (RelativeLayout) taskItemRow.findViewById(R.id.taskDueDateErrorMsgContainer);
        TextView dueDateErrorMsgTextView = (TextView) taskItemRow.findViewById(R.id.taskDueDateErrorMsg);

        // adds task to task list array
        final ProjectTaskInfoHolder
                newTask = new ProjectTaskInfoHolder(taskNameEditText, taskDueDateTextView, taskCompletedStatusBox,
                errorMsgContainer, dueDateErrorMsgTextView);
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

                // do something with the date chosen by the user
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
                // removes task from task list array, then from the user interface/layout
                taskList.remove(newTask);
                ((LinearLayout) taskItemRow.getParent()).removeView(taskItemRow);
            }
        });

        // adds task to user interface/layout
        taskListContainer.addView(taskItemRow);
    }

    /**
     * Method: validateProjectFields
     * Validates project fields and task fields for any tasks added to ensure everything has been
     * set before saving the event.
     * @return result the result of the validations
     */
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

    /**
     * Method: validateProjectName
     * Makes sure that the project title/name has been set.
     * @return result the result of the validation
     */
    private boolean validateProjectName()
    {
        boolean result = true;

        projectName = projectNameEditText.getText().toString();

        if (projectName.isEmpty())
        {
            projectNameEditText.setError(PROJECT_NAME_ERROR_MSG);
            result = false;
        }

        return result;
    }

    /**
     * Method: validateProjectDueDate
     * Makes sure that a due date has been set, and that it is later than or equal to
     * the project start date.
     * @return result the result of the validation
     */
    private boolean validateProjectDueDate()
    {
        boolean result = true;

        if (projectDueDate.equalsIgnoreCase("Select date"))
        {
            dueDateTextView.setError("");
            dueDateErrorMessage.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_msg_popup_background));
            dueDateErrorMessage.setText(DUE_DATE_ERROR_MSG_1);
            result = false;
        }

        else
        {
            try
            {
                Date startDate = DATE_FORMATTER.parse(projectStartDate);
                Date dueDate = DATE_FORMATTER.parse(projectDueDate);

                if (dueDate.before(startDate))
                {
                    dueDateTextView.setError("");
                    dueDateErrorMessage.setBackgroundDrawable(getResources().getDrawable(R.drawable.error_msg_popup_background));
                    dueDateErrorMessage.setText(DUE_DATE_ERROR_MSG_2);
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

    /**
     * Method: validateTaskName
     * Makes sure that a task name has been set.
     * @param task the task to validate
     * @return result the result of the validation
     */
    private boolean validateTaskName(ProjectTaskInfoHolder task)
    {
        boolean result = true;

        if (task.getTaskName().isEmpty())
        {
            result = false;
            task.getTaskNameEditText().setError(TASK_NAME_ERROR_MSG);
        }

        return result;
    }

    /**
     * Method: validateTaskDueDate
     * Makes sure that the task due date is set to a date later than or equal to the
     * project start date.
     * @param task the task to validate
     * @return result the result of the validation
     */
    private boolean validateTaskDueDate(ProjectTaskInfoHolder task)
    {
        boolean result = true;

        try
        {
            Date tempProjStartDate = DATE_FORMATTER.parse(projectStartDate);
            Date tempTaskDueDate = DATE_FORMATTER.parse(task.getDueDate());

            if (tempTaskDueDate.before(tempProjStartDate))
            {
                task.setError(DUE_DATE_ERROR_MSG_2);

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
        // save event, project and task data in SQLite database
        if (validateProjectFields())
        {
            // save project in sqlite project table
            int hasTask = 0;
            if(taskList.size() > 0)
            {
                hasTask = 1;
            }

            ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
            Project project = projectDAO.createProject(projectName, projectStartDate, projectDueDate,
                    hasTask);

            // save tasks in sqlite task table
            if (taskList.size() > 0)
            {
                for (ProjectTaskInfoHolder task : taskList)
                {
                    TaskDAO taskDAO = new TaskDAO(getApplicationContext());
                    Task newTask = taskDAO.createTask(project.getEventID(), task.getTaskName(),
                            task.getDueDate(), task.isTaskCompleted());
                }
            }

            if (!hasInternetConnection())
            {
                Toast.makeText(getApplicationContext(), "No internet connectivity, data only saved locally",
                        Toast.LENGTH_SHORT).show();
            }

            else
            {
                RequestQueue queue = Volley.newRequestQueue(this);
                displaySaveEventProgressDialog();

                Response.Listener<String> apiResponseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        dismissSaveEventProgressDialog();
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean error = jsonResponse.getBoolean("error");
                            if (error)
                            {
                                String errorMsg = jsonResponse.getString("errorMsg");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }

                            else
                            {
                                // finish event activity and return to calendar view
                                finish();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        // TODO: for api response testing
//                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NoConnectionError)
                        {
                            errorMsg = "Network is unreachable, please check your internet connection";
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof ServerError)
                        {
                            errorMsg = "Server error";
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }

                        dismissSaveEventProgressDialog();

                        // TODO: for request error response testing
//                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                };

                StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.day_planner_api_url),
                        apiResponseListener, errorListener)
                {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<>();

                        // project data
                        params.put("tag", TAG);

                        params.put("userID", userID);
                        params.put("type", eventType);
                        params.put("title", projectName);
                        params.put("start date", projectStartDate);
                        params.put("end date", projectDueDate);

                        // project task data
                        if (taskList.size() > 0)
                        {
                            Map<String, Map> taskMap = new HashMap<>();
                            Map<String, String> task;

                            for (int i = 0; i < taskList.size(); i++)
                            {
                                String key = "task";
                                task = new HashMap<>();
                                task.put("task name", taskList.get(i).getTaskName());
                                task.put("task due date", taskList.get(i).getDueDate());
                                task.put("task completed status", "" + taskList.get(i).isTaskCompleted());
                                taskMap.put(key + (i + 1), task);
                            }

                            JSONObject taskJsonObj = new JSONObject(taskMap);
                            params.put("tasks", taskJsonObj.toString());
                        }

                        return params;
                    }
                };

                queue.add(strRequest);
            }
        }
    }

    /**
     * Method: displaySaveEventProgressDialog
     * Displays a progress dialog while saving an event.
     */
    private void displaySaveEventProgressDialog()
    {
        if (!saveEventProgressDialog.isShowing())
        {
            saveEventProgressDialog.show();
        }
    }

    /**
     * Method: dismissSaveEventProgressDialog
     * Dismisses the 'save event' progress dialog.
     */
    private void dismissSaveEventProgressDialog()
    {
        if (saveEventProgressDialog.isShowing())
        {
            saveEventProgressDialog.dismiss();
        }
    }

    /**
     * Method: hasInternetConnection
     * Checks to see if the mobile device has an active internet connection.
     * @return isConnected the boolean result of the verification
     */
    private boolean hasInternetConnection()
    {
        ConnectivityManager connManager = (ConnectivityManager)getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
