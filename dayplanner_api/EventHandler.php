<?php
/**
 * Class: EventHandler
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 17, 2015
 *
 * This class will handle event related transactions with the Day Planner database, such as
 * saving new events, updating existing events, deleting events, and sharing events.
 */
class EventHandler
{
    private $dbConnection;

    // constructor
    function __construct()
    {
        require_once 'config.php';

        $this->dbConnection = new mysqli($host, $user, $password, $database);
    }

    private function saveGeneralEventInfo()
    {

    }

    public function saveNewProjectEvent()
    {
        // save general event info
        $this->saveGeneralEventInfo();

        // save specific event-type info
        // save user_event info
        // save task info

        return true;
    }

    public function saveNewAppointmentEvent()
    {
        $this->saveGeneralEventInfo();
    }

    public function updateProjectEvent()
    {

    }

    public function updateAppointmentEvent()
    {

    }

    public function deleteEvent()
    {

    }

    public function shareEvent()
    {

    }

    public function shareCalendar()
    {

    }
}