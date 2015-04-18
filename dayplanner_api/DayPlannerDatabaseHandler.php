<?php

/**
 * Class DayPlannerDatabaseHandler
 * @author Joshua Campbell
 * @version 1.0
 * Course: ITEC 4860 Spring 2015
 * Written: March 23, 2015
 *
 * This class will handle transactions with the Day Planner database, such as
 * user existence verification, user registration, and user authentication. This
 * class will eventually provide functionality to save and retrieve calendar
 * event data.
 */
class DayPlannerDatabaseHandler
{
	private $dbConnection;
	
	// constructor
	function __construct()
    {
        require_once 'config.php';

        $this->dbConnection = new mysqli($host, $user, $password, $database);
	}

    /**
     * Method: doesUserExist
     * Checks if user already exists/is registered in Day Planner database by querying
     * the database for a user with either the specified username or email or both.
     * @param $username
     * @param $email
     * @return bool|mysqli_result
     */
    public function doesUserExist($username, $email)
    {
        $query = "SELECT username, email
                  FROM user
                  WHERE username = '$username'
                  OR email = '$email'";

        $result = mysqli_query($this->dbConnection, $query);
        $numRows = mysqli_num_rows($result);

        if ($numRows > 0) // user exists
        {
            $usernameMatches = false;
            $emailMatches = false;

            foreach ($result as $user)
            {
                if ($user["username"] == $username)
                {
                    $usernameMatches = true;
                }

                if ($user["email"] == $email)
                {
                    $emailMatches = true;
                }
            }

            if ($usernameMatches && $emailMatches)
            {
                $this->dbConnection->close();
                return "Username and email already exist.";
            }

            else if ($usernameMatches)
            {
                $this->dbConnection->close();
                return "Username already exists.";
            }

            else
            {
                $this->dbConnection->close();
                return "Email already exists.";
            }
        }

        else // user doesn't exist
        {
            return false;
        }
    }

    /**
     * Method: storeUser
     * Registers/stores user data in Day Planner database and returns user info.
     * @param $username
     * @param $email
     * @param $password
     * @return array|bool|null
     */
	public function storeUser($username, $email, $password)
    {
		$hashedPassword = $this->hashPassword($password);

        $storeUserQuery = "INSERT INTO user(username, email, hashed_password, created_at)
                           VALUES('$username', '$email', '$hashedPassword', NOW())";

		$result = mysqli_query($this->dbConnection, $storeUserQuery);

		if ($result) // user info stored successfully
        {
			// get user details 
			$userID = mysqli_insert_id($this->dbConnection); // last inserted id

            $getUserQuery = "SELECT *
                             FROM user
                             WHERE user_id = $userID";

			$result = mysqli_query($this->dbConnection, $getUserQuery);
            $this->dbConnection->close();

			// return user details
			return mysqli_fetch_array($result);
		}

		else
        {
            $this->dbConnection->close();
			return false;
		}
	}

    /**
     * Method: authenticateUser
     * Authenticates user by querying the database to see if the specified
     * username or email and password match a user in the database.
     * @param $usernameOrEmail
     * @param $password
     * @return array|bool|null
     */
	public function authenticateUser($usernameOrEmail, $password)
    {
        $userAuthenticationQuery = "SELECT *
                                    FROM user
                                    WHERE username = '$usernameOrEmail'
                                    OR email = '$usernameOrEmail'";

		$result = mysqli_query($this->dbConnection, $userAuthenticationQuery) or die($this->dbConnection->error);

		$numRows = mysqli_num_rows($result);

		if ($numRows > 0) // user found
        {
			$user = mysqli_fetch_array($result);

			if ($this->verifyPassword($password, $user['hashed_password'])) // password is correct
            {
                $this->dbConnection->close();
				return $user;
			}

            else // password failed verification
            {
                $this->dbConnection->close();
                return false;
            }
		}

		else // user not found
        {
            $this->dbConnection->close();
			return false;
		}
	}

    /**
     * Method: hashPassword
     * Hashes the user's password using PHP's hashing API. If successful, the output contains a concatenation of the
     * algorithm used, the cost, the salt, and the hash value. Otherwise the output is 'false'.
     * @param $password
     * @return bool|string
     */
	private function hashPassword($password)
    {
        return password_hash($password, PASSWORD_BCRYPT);
	}

    /**
     * Method: verifyPassword
     * Verifies the user's password using PHP's hashing API's verify method.
     * @param $passwordEntered
     * @param $hashedPassword
     * @return bool
     */
	private function verifyPassword($passwordEntered, $hashedPassword)
    {
        return password_verify($passwordEntered, $hashedPassword);
	}
}