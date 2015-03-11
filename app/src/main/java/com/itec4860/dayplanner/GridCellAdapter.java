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

/**Class: GridCellAdapter
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: March 6, 2015
 *
 * This class will...
 *
 * Purpose: This class ...
 */

public class GridCellAdapter extends BaseAdapter
{
    private final Context CONTEXT;
    private final List<DateInfoHolder> GRID_CELL_INFO_LIST = new ArrayList<DateInfoHolder>();

    private int today;
    private String selectedDate;

    /**
     * Constructor: GridCellAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     * @param today today's date
     */
    public GridCellAdapter(Context context, int today)
    {
        super();
        this.CONTEXT = context;
        this.today = today;
    }

    /**
     * Constructor: GridCellAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     * @param today today's date
     * @param selectedDate the day to display as selected
     */
    public GridCellAdapter(Context context, int today, String selectedDate)
    {
        super();
        this.CONTEXT = context;
        this.today = today;
        this.selectedDate = selectedDate;
    }

    public void addItem(DateInfoHolder dateInfoHolder)
    {
        GRID_CELL_INFO_LIST.add(dateInfoHolder);
    }

    /**
     * Method getCount
     * Returns the size of the list containing DateInfoHolder objects.
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
     * Returns the view of a single cell in the calendar grid.
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
        gridCellText.setTextColor(Color.parseColor(dateInfoHolder.getDateColor()));
        gridCellText.setText(dateInfoHolder.getDay());

        if (dateInfoHolder.getDay().equals(String.valueOf(today)))
        {
            gridCellText.setTextAppearance(CONTEXT, android.R.style.TextAppearance_Large);
            dateInfoHolder.getSelectedDayImage().setImageResource(R.drawable.circle_solid);
        }

        else if (dateInfoHolder.getDate().equalsIgnoreCase(selectedDate))
        {
            dateInfoHolder.getSelectedDayImage().setImageResource(R.drawable.circle_outline);
        }

        else
        {
            dateInfoHolder.getSelectedDayImage().setImageResource(R.drawable.circle_invisible);
        }

        cell.setTag(dateInfoHolder);

        return cell;
    }

    /**
     * Method: getSelectedDate
     * Returns the day that has been selected/clicked on by the user.
     * @return the selected day
     */
    public String getSelectedDate()
    {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate)
    {
        this.selectedDate = selectedDate;
    }
}
