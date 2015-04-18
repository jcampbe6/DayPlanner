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

	// include database handler
    include_once 'DayPlannerDatabaseHandler.php';
	$databaseHandler = new DayPlannerDatabaseHandler();
	
	// response Array in JSON format
	$jsonResponse = array("error" => false);

    // check "tag" for request type
    // so far the possible request types are: 'register' and 'login'
    if ($tag == "register")
    {
        $regUsername = $_POST["username"];
		$regEmail = $_POST["email"];
		$regPassword = $_POST["password"];

		// check if user already exists
        $errorMsg = $databaseHandler->doesUserExist($regUsername, $regEmail);

		if ($errorMsg) // user already exists --> respond with error message
        {
            $jsonResponse["error"] = true;
            $jsonResponse["errorMsg"] = $errorMsg;

			echo json_encode($jsonResponse);
		}

		else // there is no error message --> user doesn't exist --> store user
        {
			$registeredUser = $databaseHandler->storeUser($regUsername, $regEmail, $regPassword);

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
        // Request type is check Login
        $loginUsernameOrEmail = $_POST['usernameOrEmail'];
        $loginPassword = $_POST['password'];

        // check for user
        $authenticatedUser = $databaseHandler->authenticateUser($loginUsernameOrEmail, $loginPassword);

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

	else // received a tag other than 'register' or 'login'
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