<?php
/**
 * Class: EventHandler
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: April 17, 2015
 *
 * This class will handle event related transactions with the Day Planner database, such as
 * saving new events, updating existing events, deleting events, and sharing events and
 * retrieving events.
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

    /**
     * Method: saveEvent
     * Saves event data in Day Planner database.
     * @param $eventDataArray
     */
    public function saveEvent($eventDataArray)
    {
        // event table info
        $eventTitle = $eventDataArray['title'];
        $eventType = $eventDataArray['type'];
        $eventId = $this->saveGeneralEventInfo($eventTitle, $eventType);

        /*
         * if general event info is saved and the event id is retrieved successfully, then proceed to check
         * for event type and save the event accordingly
         */
        if ($eventId)
        {
            // user_event table info
            $userId = $eventDataArray['userID'];
            $this->saveUserEventInfo($userId, $eventId, true);

            if ($eventType == "project")
            {
                // project table info
                $startDate = $eventDataArray['start_date'];
                $endDate = $eventDataArray['end_date'];
                $hasTask = false;

                // check for tasks associated with the project
                if (isset($eventDataArray['tasks']))
                {
                    $hasTask = true;
                    $tasks = json_decode($eventDataArray['tasks'], true);
                    $this->saveTasks($eventId, $tasks);
                }

                $this->saveProject($eventId, $startDate, $endDate, $hasTask);
            }

            else if ($eventType == "appointment")
            {
                // todo: save appointment
            }
        }
    }

    /**
     * Method: saveGeneralEventInfo
     * Saves the main event info (title and type) to the event table in the Day Planner database and
     * returns the id of the event that was saved.
     * @param $eventTitle
     * @param $eventType
     * @return int|string
     */
    private function saveGeneralEventInfo($eventTitle, $eventType)
    {
        $saveEventQuery = "INSERT INTO event(title, type)
                           VALUES('$eventTitle', '$eventType')";

        $saveEventResult = mysqli_query($this->dbConnection, $saveEventQuery);
        return $eventId = mysqli_insert_id($this->dbConnection); // last inserted id
    }

    /**
     * Method: saveUserEventInfo
     * Saves data to the user_event table in the Day Planner database to associate each event with a user and
     * specify if the user is the creator/owner of the event.
     * @param $userId
     * @param $eventId
     * @param $isOwner
     */
    private function saveUserEventInfo($userId, $eventId, $isOwner)
    {
        $saveUserEventQuery = "INSERT INTO user_event(user_id, event_id, is_owner)
                               VALUES('$userId', '$eventId', '$isOwner')";

        $saveUserEventResult = mysqli_query($this->dbConnection, $saveUserEventQuery);
    }

    /**
     * Method: saveProject
     * Saves data corresponding to an event of type 'project' to the project table in the Day Planner database.
     * @param $eventId
     * @param $startDate
     * @param $endDate
     * @param $hasTask
     */
    private function saveProject($eventId, $startDate, $endDate, $hasTask)
    {
        $saveProjectQuery = "INSERT INTO project(event_id, start_date, end_date, has_task)
                             VALUES('$eventId', '$startDate', '$endDate', '$hasTask')";

        $saveProjectResult = mysqli_query($this->dbConnection, $saveProjectQuery);
    }

    /**
     * Method: saveTasks
     * Saves task data associated with a project to the task table in the Day Planner database.
     * @param $projectId
     * @param $taskArray
     */
    private function saveTasks($projectId, $taskArray)
    {
        foreach ($taskArray as $task)
        {
            $title = $task['task name'];
            $dueDate = $task['task due date'];
            $completed = ($task['task completed status']);

            $saveTaskQuery = "INSERT INTO task(event_id, title, due_date, completed)
                              VALUES('$projectId', '$title', '$dueDate', '$completed')";

            $saveTaskResult = mysqli_query($this->dbConnection, $saveTaskQuery);
        }
    }

    public function retrieveEvents($userId, $date)
    {
        $retrieveEventsQuery = "SELECT *
                               FROM project
                               WHERE EXISTS
                                (
                                  SELECT event_id
                                  FROM user_event
                                  WHERE user_id =  '$userId'
                                )
                               AND start_date =  '$date'";

        return $retrieveEventsResult = mysqli_query($this->dbConnection, $retrieveEventsQuery);
    }

    public function saveAppointment()
    {

    }

    public function updateProject()
    {

    }

    public function updateAppointment()
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