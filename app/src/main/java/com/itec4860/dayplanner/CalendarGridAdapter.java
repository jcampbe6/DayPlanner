package com.itec4860.dayplanner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**Class: CalendarGridAdapter
 * @author Joshua Campbell
 * @version 1.1
 * Course: ITEC 4860 Spring 2015
 * Written: March 6, 2015
 *
 * This class will contain a list of all the objects representing a single day in a calendar and
 * will construct the view for each cell representing a day and display them on a calendar.
 *
 * Purpose: This class will serve as a custom adapter for the Day Planner application to populate a
 * GridView that represents a calendar with data.
 */

public class CalendarGridAdapter extends BaseAdapter
{
    private final Context CONTEXT;
    private final List<DateInfoHolder> GRID_CELL_INFO_LIST = new ArrayList<DateInfoHolder>();

    private String todaysDate;
    private String selectedDate;
    private int selectedItemPosition;

    /**
     * Constructor: CalendarGridAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     * @param todaysDate today's date
     */
    public CalendarGridAdapter(Context context, String todaysDate)
    {
        super();
        this.CONTEXT = context;
        this.todaysDate = todaysDate;
    }

    /**
     * Constructor: CalendarGridAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     * @param todaysDate today's date
     * @param selectedDate the day to display as selected
     */
    public CalendarGridAdapter(Context context, String todaysDate, String selectedDate)
    {
        super();
        this.CONTEXT = context;
        this.todaysDate = todaysDate;
        this.selectedDate = selectedDate;
    }

    /**
     * Method: addItem
     * Adds a DateInfoHolder object to the list of date info objects.
     * @param dateInfoHolder the date info object
     */
    public void addItem(DateInfoHolder dateInfoHolder)
    {
        GRID_CELL_INFO_LIST.add(dateInfoHolder);
    }

    /**
     * Method getCount
     * Returns the size of the list containing DateInfoHolder objects. Note: this method is required
     * to be implemented, but is not used.
     * @return the size of the list
     */
    @Override
    public int getCount()
    {
        return GRID_CELL_INFO_LIST.size();
    }

    /**
     * Method: getItem
     * Returns the item at the specified position in the list containing DateInfoHolder objects.
     * @param position the position in the list
     * @return the item
     */
    @Override
    public DateInfoHolder getItem(int position)
    {
        return GRID_CELL_INFO_LIST.get(position);
    }

    /**
     * Method: getItemId
     * Returns the id of the cell at a specified position. Here, the id is the same as the position.
     * Note: this method is required to be implemented, but is not used.
     * @param position the specified cell position
     * @return position the id of the specified cell
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Method: getView
     * Returns the view of a single cell that represents a day in the calendar grid.
     * @param position the position of the cell
     * @param convertView the cell view used to inflate the layout
     * @param parent the cell's parent view (the grid view)
     * @return cell the view of the cell
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View cell = convertView;

        if (cell == null)
        {
            LayoutInflater inflater = (LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = inflater.inflate(R.layout.grid_cell, parent, false);
        }

        DateInfoHolder dateInfoHolder = getItem(position);
        dateInfoHolder.setSelectedDayImage((ImageView) cell.findViewById(R.id.selectedDayImage));

        TextView gridCellText = (TextView) cell.findViewById(R.id.calendarDayText);
        gridCellText.setTextColor(dateInfoHolder.getDateTextColor());
        gridCellText.setText(dateInfoHolder.getDay());

        TextView gridCellNumEvents = (TextView) cell.findViewById(R.id.numEvents);

        if (dateInfoHolder.getDate().equals(todaysDate))
        {
            dateInfoHolder.getSelectedDayImage().setImageResource(R.drawable.circle_solid);
            gridCellText.setTextColor(CONTEXT.getResources().getColor(R.color.todaysDateColor));

        }

        else if (dateInfoHolder.getDate().equalsIgnoreCase(selectedDate))
        {
            dateInfoHolder.getSelectedDayImage().setImageResource(R.drawable.circle_outline);
        }

        else
        {
            dateInfoHolder.getSelectedDayImage().setImageResource(R.drawable.circle_invisible);
        }

        if (dateInfoHolder.getEventCount() > 0)
        {
            gridCellNumEvents.setText("" + dateInfoHolder.getEventCount());
        }

        cell.setTag(dateInfoHolder);

        return cell;
    }

    /**
     * Method: getSelectedDate
     * Returns the date that has been selected/clicked on by the user.
     * @return the selected date
     */
    public String getSelectedDate()
    {
        return selectedDate;
    }

    /**
     * Method: setSelectedDate
     * Sets the selected date to the String date passed.
     * @param selectedDate the date to set as selected
     */
    public void setSelectedDate(String selectedDate)
    {
        this.selectedDate = selectedDate;
        setSelectedItemPosition(this.selectedDate);
    }

    public void setSelectedItemPosition(String date)
    {
        for (int i = 0; i < GRID_CELL_INFO_LIST.size(); i++)
        {
            if (getItem(i).getDate().equalsIgnoreCase(date))
            {
                selectedItemPosition = i;
            }
        }
    }

    public int getSelectedItemPosition()
    {
        return selectedItemPosition;
    }
}
