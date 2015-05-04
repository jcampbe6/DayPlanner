package com.itec4860.dayplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itec4860.dayplanner.sqliteDatabase.Event;
import com.itec4860.dayplanner.sqliteDatabase.EventDAO;
import com.itec4860.dayplanner.sqliteDatabase.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: EventListAdapter
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: May 04, 2015
 * 
 * This class...
 * 
 * Purpose: 
 */
public class EventListAdapter extends BaseAdapter
{
    private List<Project> projectList = new ArrayList<>();
    private Context context;

    public EventListAdapter(Context context)
    {
        this.context = context;
    }

    public void addProjectList(List<Project> projectList)
    {
        this.projectList = projectList;
    }

    @Override
    public int getCount()
    {
        return projectList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return projectList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View listItem = convertView;

        if (listItem == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItem = inflater.inflate(R.layout.project_calendar_list_item, parent, false);
        }

        Project project = (Project) getItem(position);
        EventDAO eventDAO = new EventDAO(context);
        Event event = eventDAO.getEventById(project.getProjectID());

        TextView projectTitleTextView = (TextView) listItem.findViewById(R.id.projectListItemTitle);
        TextView projectStartDateTextView = (TextView) listItem.findViewById(R.id.projectListItemStartDate);
        TextView projectDueDateTextView = (TextView) listItem.findViewById(R.id.projectListItemDueDate);

        projectTitleTextView.setText(event.getTitle());
        projectStartDateTextView.setText(project.getStartDate());
        projectDueDateTextView.setText(project.getEndDate());

        return listItem;
    }
}
