<?php
/**
* This file will handle all API requests.
* Accepts POST requests.
* 
* Each request will be identified by a "tag".
* The response will be a JSON object.
*/
    
/**
* check for the POST "tag" parameter
*/
if (isset($_POST["tag"]) && $_POST["tag"] != "")
{
	// get tag
	$tag = $_POST["tag"];
	
	// response Array in JSON format
	$jsonResponse = array("error" => false);

    // check "tag" for request type
    // so far the possible request types are: 'register', 'login' and 'save event'
    if ($tag == "register")
    {
        include_once 'UserRegLoginHandler.php';
        $userRegHandler = new UserRegLoginHandler();

        $regUsername = $_POST["username"];
		$regEmail = $_POST["email"];
		$regPassword = $_POST["password"];

		// check if user already exists
        $errorMsg = $userRegHandler->doesUserExist($regUsername, $regEmail);

		if ($errorMsg) // user already exists --> respond with error message
        {
            $jsonResponse["error"] = true;
            $jsonResponse["errorMsg"] = $errorMsg;

			echo json_encode($jsonResponse);
		}

		else // there is no error message --> user doesn't exist --> store user
        {
			$registeredUser = $userRegHandler->storeUser($regUsername, $regEmail, $regPassword);

			if ($registeredUser) // user stored successfully
            {
				$jsonResponse["userID"] = $registeredUser["user_id"];
                $jsonResponse["username"] = $registeredUser["username"];
				$jsonResponse["email"] = $registeredUser["email"];
				$jsonResponse["created_at"] = $registeredUser["created_at"];
				echo json_encode($jsonResponse);
			}

			else // failed to store user
            {
				$jsonResponse["error"] = true;
				$jsonResponse["errorMsg"] = "Error occurred in registration";
				echo json_encode($jsonResponse);
			}
		}
	}

    else if ($tag == "login")
    {
        include_once 'UserRegLoginHandler.php';
        $userLoginHandler = new UserRegLoginHandler();

        // Request type is check Login
        $loginUsernameOrEmail = $_POST['usernameOrEmail'];
        $loginPassword = $_POST['password'];

        // check for user
        $authenticatedUser = $userLoginHandler->authenticateUser($loginUsernameOrEmail, $loginPassword);

        if ($authenticatedUser) // user email and password match
        {
            $jsonResponse["userID"] = $authenticatedUser["user_id"];
            $jsonResponse["username"] = $authenticatedUser["username"];
            $jsonResponse["email"] = $authenticatedUser["email"];
            $jsonResponse["created_at"] = $authenticatedUser["created_at"];
            echo json_encode($jsonResponse);
        }

        else // user not found
        {
            $jsonResponse["error"] = true;
            $jsonResponse["errorMsg"] = "Incorrect username or password";
            echo json_encode($jsonResponse);
        }
    }

    else if ($tag == "save event")
    {
        include_once 'EventHandler.php';
        $saveEventHandler = new EventHandler();

        unset($_POST['tag']); // removes tag to send only what is needed to saveEvent
        $eventData = $_POST;
        $saveEventHandler->saveEvent($eventData);
        echo json_encode($jsonResponse);
    }

    else if ($tag == "retrieve events")
    {
        include_once 'EventHandler.php';
        $retrieveEventHandler = new EventHandler();

        $userId = $_POST['userID'];
        $date = $_POST['date'];

        $events = $retrieveEventHandler->retrieveEvents($userId, $date);

        $eventCount = 0;
        foreach ($events as $event)
        {
            $eventCount++;
            $jsonResponse["event$eventCount"] = $event;
        }

        $jsonResponse["eventCount"] = $eventCount;

        echo json_encode($jsonResponse);

    }

    else if ($tag == "retrieve event count")
    {
        include_once 'EventHandler.php';
        $retrieveEventCountHandler = new EventHandler();

        $userId = $_POST['userID'];
        $date = $_POST['date'];

        $events = $retrieveEventHandler->retrieveEvents($userId, $date);

        $eventCount = 0;
        foreach ($events as $event)
        {
            $eventCount++;
        }

        $jsonResponse["eventCount"] = $eventCount;

        echo json_encode($jsonResponse);
    }

	else // received a tag other than 'register', 'login', 'save event' or 'retrieve events'
    {
        $jsonResponse["error"] = true;
        $jsonResponse["errorMsg"] = "Invalid request.";
        echo json_encode($jsonResponse);
	}
}

else
{
    echo "Access Denied";
}