package com.itec4860.dayplanner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class GridCellAdapter extends BaseAdapter implements View.OnClickListener
{
    private final Context CONTEXT;
    private final List<DateInfoHolder> GRID_CELL_INFO_LIST = new ArrayList<DateInfoHolder>();

    private int currentDay;
    private int selectedDay;

    /**
     * Constructor: GridCellAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     */
    public GridCellAdapter(Context context, int currentDay)
    {
        super();
        this.CONTEXT = context;
        this.currentDay = currentDay;
    }

    /**
     * Constructor: GridCellAdapter
     * Constructs an adapter to populate each day in a calendar grid.
     * @param context the Day Planner application environment
     * @param selectedDay the day to display as selected
     */
    public GridCellAdapter(Context context, int currenday, int selectedDay)
    {
        super();
        this.CONTEXT = context;
        this.currentDay = currenday;
        this.selectedDay = selectedDay;

        //TODO fillCalendar(month, year);
    }

    public void addItem(DateInfoHolder dateInfoHolder)
    {
        GRID_CELL_INFO_LIST.add(dateInfoHolder);
    }

    //TODO
    @Override
    public int getCount()
    {
        return GRID_CELL_INFO_LIST.size();
    }

    //TODO
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

        final DateInfoHolder dateInfoHolder = GRID_CELL_INFO_LIST.get(position);
        dateInfoHolder.setSelectedDayIndicator((ImageButton) cell.findViewById(R.id.selectedDayIndicator));

        Button gridCell = (Button) cell.findViewById(R.id.calendarDayGridCell);
        gridCell.setTextColor(Color.parseColor(dateInfoHolder.getDateColor()));
        gridCell.setText(dateInfoHolder.getDay());
        gridCell.setTag(dateInfoHolder);
        gridCell.setOnClickListener(this);

        if (dateInfoHolder.getDay().equals(String.valueOf(currentDay)))
        {
            gridCell.setTextAppearance(CONTEXT, android.R.style.TextAppearance_Large);
            dateInfoHolder.getSelectedDayIndicator().setImageResource(R.drawable.circle_solid);
        }

        if (Integer.parseInt(dateInfoHolder.getDay()) == selectedDay)
        {
            dateInfoHolder.getSelectedDayIndicator().setFocusable(true);
            dateInfoHolder.getSelectedDayIndicator().setFocusableInTouchMode(true);
            dateInfoHolder.getSelectedDayIndicator().requestFocus();
        }

        return cell;
    }

    public int getSelectedDay()
    {
        return selectedDay;
    }

    /**
     * Method: onClick
     * Will eventually perform calendar specific functions, such as create a new calendar event.
     * @param view the cell that was clicked
     */
   //TODO @Override
    public void onClick(View view)
    {
        DateInfoHolder dateInfoHolder = (DateInfoHolder) view.getTag();
        dateInfoHolder.getSelectedDayIndicator().setFocusable(true);
        dateInfoHolder.getSelectedDayIndicator().setFocusableInTouchMode(true);
        dateInfoHolder.getSelectedDayIndicator().requestFocus();

        Toast.makeText(CONTEXT, dateInfoHolder.getDate(), Toast.LENGTH_SHORT).show();

        selectedDay = Integer.parseInt(dateInfoHolder.getDay());
    }
}
