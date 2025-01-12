# Enigma

This is a task and user management REST API built with Spring Boot. It supports operations like creating, updating,
deleting, and retrieving tasks and users. The API features JWT-based authentication, filtering, sorting, pagination,
and user-task assignment.

## Features
Task Management: Manage tasks with detailed or basic information.
User Management: Manage users, including their tasks and passwords.
JWT Authentication: Secure endpoints based on roles (USER, ADMIN).
Filtering, Sorting, and Pagination: Available for both tasks and users.
Data Initialization: Handled using Liquibase.
Docker Support: Deploy with Docker Compose.
CI/CD: GitHub Actions pipeline to build Docker images.

## Prerequisites
Before you begin, ensure you have the following installed:

Java 21+
Docker

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/zuku0404/ManagerWithTest.git
    ```
2. Navigate to the project directory:
    ```bash
    cd ManagerWithTest
    ```
## Running the Application
You have two options for running the application. Choose the one that fits your setup.

### 1. Option 1: Use Docker Compose (All-in-One)
1. Build the Application and Docker Images:
    ```bash
     docker-compose -f app.yaml build --no-cache enigma
    ```
2. Run the Application:
    ```bash
     docker-compose -f app.yaml up
    ```
This approach uses app.yaml to build and start both the application and the database in one step.

### 2. Option 2: Separate Database Setup
1. Start the MySQL Database:
    ```bash
     docker-compose -f mySqldev.yaml up
    ```
2. Build the Application: If using Gradle:
    ```bash
     ./gradlew build
    ```
3. Run the Application: Start the application using your preferred method, such as:
    ```bash
     java -jar build/libs/enigma-0.0.1-SNAPSHOT.jar
    ```
This option allows for separate control of the database container and application execution.

## API Endpoints

## FOR TASK

### Get All Tasks (Detailed)

- **URL:** `/api/tasks/detailed`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
   - `Accept: application/json`
      - **Query Parameters:**
        - user_id (Optional): Filter tasks assigned to a specific user.
        - status (Optional): Filter tasks by status (e.g., IN_PROGRESS, COMPLETED).
        - page (Optional, Default: 0): Pagination support.
        - sort (Optional, Default: false): Enable sorting.
        - sort_direction (Optional, Default: ASC): Sorting direction.
        - **Response:**
           - `200 OK`
              * Example:
                ```json
                  [
                      {
                         "id": 2,
                         "title": "title_2",
                         "description": "description_2",
                         "taskStatus": "IN_PROGRESS",
                         "deadline": "2024-10-25",
                         "users": [
                                     {
                                     "id": 2,
                                     "name": "user",
                                     "lastName": "user",
                                     "email": "user1@wp.pl"
                                     }
                                  ]
                        }
                  ]
                ```

### Get All Tasks (Basic)

- **URL:** `/api/tasks/basic`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
   - `Accept: application/json`
      - **Query Parameters:**
         - user_id (Optional): Filter tasks assigned to a specific user.
         - status (Optional): Filter tasks by status (e.g., IN_PROGRESS, COMPLETED).
         - sort (Optional, Default: false): Enable sorting.
         - sort_direction (Optional, Default: ASC): Sorting direction.
      - **Response:**
         - `200 OK`
            * Example:
              ```json
                 [
                    {
                    "id": 1,
                    "title": "title_1",
                    "description": "description_1",
                    "taskStatus": "TO_DO",
                    "deadline": "2024-11-01"
                    }
                 ]
              ```
              
### Get All Unsigned Tasks

- **URL:** `/api/tasks/unsigned`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
   - `Accept: application/json`
      - **Query Parameters:**
         - page (Optional, Default: 0): Pagination support.
         - status (Optional): Filter tasks by status (e.g., IN_PROGRESS, COMPLETED).
         - sort (Optional, Default: false): Enable sorting.
         - sort_direction (Optional, Default: ASC): Sorting direction.
      - **Response:**
         - `200 OK`
            * Example:
              ```json
                 [
                    {
                    "id": 1,
                    "title": "title_1",
                    "description": "description_1",
                    "taskStatus": "TO_DO",
                    "deadline": "2024-11-01"
                    }
                 ]
              ```

### Get Single Task By Id

- **URL:** `/api/tasks/{id}`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
   - `Accept: application/json`
     - **Response:**
       - `200 OK`
         * Example:
           ```json
                [
                    {
                       "id": 2,
                       "title": "title_2",
                       "description": "description_2",
                       "taskStatus": "IN_PROGRESS",
                       "deadline": "2024-10-25",
                       "users": [
                                   {
                                   "id": 2,
                                   "name": "user",
                                   "lastName": "user",
                                   "email": "user1@wp.pl"
                                   }
                                ]
                      }
                ]
             ```

### Get Single Task By Title

- **URL:** `/api/tasks/{title}`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
   - `Accept: application/json`
      - **Response:**
         - `200 OK`
            * Example:
              ```json
                   [
                       {
                          "id": 2,
                          "title": "title_2",
                          "description": "description_2",
                          "taskStatus": "IN_PROGRESS",
                          "deadline": "2024-10-25",
                          "users": [
                                      {
                                      "id": 2,
                                      "name": "user",
                                      "lastName": "user",
                                      "email": "user1@wp.pl"
                                      }
                                   ]
                         }
                   ]
                ```

### Create New Task

- **URL:** `/api/tasks`
- **Method:** `POST`
- **Authentication** Admin or User role required
- **Headers:**
   - `Accept: application/json`
   - `Authorization: Bearer <JWT_TOKEN>`
   - **Request:**
         * Example:
           ```json
               {
                  "title": "title_120",
                  "description": "description_1",
                  "taskStatus": "TO_DO",
                  "deadline": "2025-11-01",
                  "usersIds": [1]
               }
           ```
      - **Response:**
         - `201 Created`
            * Example:
              ```json
                {
                    "id": 13,
                    "title": "title_120",
                    "description": "description_1",
                    "taskStatus": "TO_DO",
                    "deadline": "2025-11-01",
                    "users": [
                                {
                                   "id": 1,
                                   "name": "admin",
                                   "lastName": "adminek",
                                   "email": "admin@wp.pl"
                                }
                             ]
                 }
              ```

### Update Task

- **URL:** `/api/tasks/{id}`
- **Method:** `PUT`
- **Authentication** Admin or User role required
- **Headers:**
   - `Accept: application/json`
   - `Authorization: Bearer <JWT_TOKEN>`
      - **Request:**
         * Example:
           ```json
               {
                  "title": "title_120",
                  "description": "description_1",
                  "taskStatus": "TO_DO",
                  "deadline": "2025-11-01",
                  "usersIds": [1]
               }
           ```
      - **Response:**
         - `200 OK`
            * Example:
              ```json
                {
                    "id": 13,
                    "title": "title_120",
                    "description": "description_1",
                    "taskStatus": "TO_DO",
                    "deadline": "2025-11-01",
                    "users": [
                                {
                                   "id": 1,
                                   "name": "admin",
                                   "lastName": "adminek",
                                   "email": "admin@wp.pl"
                                }
                             ]
                 }
              ```

### Change Task Status

- **URL:** `/api/tasks/{id}/status`
- **Method:** `PATCH`
- **Authentication** Admin or User role required
  - **Headers:**
     - `Accept: application/json`
     - `Authorization: Bearer <JWT_TOKEN>`
        - **Request:**
          A single string value representing the status to which the task should be updated.
            Valid values:
               - "TO_DO"
               - "IN_PROGRESS"
               - "DONE"
          * Example: 
             ```json
               "TO_DO"
               ```
        - **Response:**
          - `200 OK`
             * Example:
               ```json
                  {
                    "id": 13,
                    "title": "title_120",
                    "description": "description_1",
                    "taskStatus": "TO_DO",
                    "deadline": "2025-11-01",
                    "users": [
                                {
                                   "id": 1,
                                   "name": "admin",
                                   "lastName": "adminek",
                                   "email": "admin@wp.pl"
                                }
                             ]
                 }
               ```

### Assign or Remove Users from Task

- **URL:** `/api/tasks/{id}/users`
- **Method:** `PATCH`
- **Authentication** Admin or User role required
- **Headers:**
   - `Accept: application/json`
   - `Authorization: Bearer <JWT_TOKEN>`
      - **Request:**
         A JSON object containing the following fields:
            - userId: (Required) The ID of the user to be added or removed from the task.
            - action: (Required) A string indicating the action to perform.
               - Valid values:
                  -  "ADD": Assign the user to the task.
                  -  "REMOVE": Unassign the user from the task.
         * Example:
            ```json
               {
                 "userId": 2,
                 "action": "ADD"
               }
              ```
      - **Response:**
         - `200 OK`
            * Example:
              ```json
                 {
                   "id": 13,
                   "title": "title_120",
                   "description": "description_1",
                   "taskStatus": "TO_DO",
                   "deadline": "2025-11-01",
                   "users": [
                               {
                                  "id": 1,
                                  "name": "admin",
                                  "lastName": "adminek",
                                  "email": "admin@wp.pl"
                               }
                            ]
                }
              ```


### Delete a Task

- **URL:** `/api/tasks/{id}`
- **Method:** `DELETE`
- **Authentication** Admin or User role required
- **Headers:**
   - `Accept: application/json`
   - `Authorization: Bearer <JWT_TOKEN>`
     - **Response:**
       - `200 OK`
         * Example: 
            ```json
              {
                  "message": "Task deleted successfully."
              }
           ```
       - `404 Not Found`
         * Example:
            ```json
              {
                  "status": 404,
                  "message": "Task not found."
              }
           ```

## FOR USER