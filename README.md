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
     docker-compose -f mySql-dev.yml up -d
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

- **Base URL:** http://localhost:8085/

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
  - A single string value representing the status to which the task should be updated.
    - Valid values:
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
  - A JSON object containing the following fields:
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

### Get Current User

- **URL:** `/api/users/current-user`
- **Method:** `GET`
- **Authentication** Admin or User role required
- **Headers:**
    - `Accept: application/json`
    - `Authorization: Bearer <JWT_TOKEN>`

- **Response:**
  - `200 OK`
  * Example:
    ```json
      {
        "id": 1,
        "firstName": "admin",
        "lastName": "adminek",
        "email": "admin@wp.pl",
        "role": "ROLE_ADMIN",
        "tasks": []
        }
    ```

### Get All Users (Detailed)

- **URL:** `/api/users/detailed`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
    - `Accept: application/json`

- **Query Parameters:**
    - firstName (Optional): Filter users by first name.
    - lastName (Optional): Filter users by last name.
    - page (Optional, Default: 0): Specify the page number for pagination.

- **Response:**
  - `200 OK`
  * Example:
    ```json
      [
          {
              "id": 1,
              "firstName": "admin",
              "lastName": "adminek",
              "email": "admin@wp.pl",
              "role": "ROLE_ADMIN",
              "tasks": []
          },
          {
              "id": 2,
              "firstName": "user",
              "lastName": "user",
              "email": "user1@wp.pl",
              "role": "ROLE_USER",
              "tasks": [
                          {
                              "id": 2,
                              "title": "title_2",
                              "description": "description_2",
                              "taskStatus": "IN_PROGRESS",
                              "deadline": "2024-10-25"
                          },
                          {
                              "id": 3,
                              "title": "title_3",
                              "description": "description_3",
                              "taskStatus": "DONE",
                              "deadline": "2024-10-20"
                          }
                       ]
        }
      ]
    ```

### Get All Users (Basic)

- **URL:** `/api/users/basic`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
    - `Accept: application/json`

- **Query Parameters:**
    - firstName (Optional): Filter users by first name.
    - lastName (Optional): Filter users by last name.

- **Response:**
  - `200 OK`
  * Example:
     ```json
            [
                {
                    "id": 1,
                    "firstName": "admin",
                    "lastName": "adminek",
                    "email": "admin@wp.pl",
                    "role": "ROLE_ADMIN"
                },
                {
                    "id": 2,
                    "firstName": "user",
                    "lastName": "user",
                    "email": "user1@wp.pl",
                    "role": "ROLE_USER"
                }
            ]
    ```

### Get Single User By Id

- **URL:** `/api/users/{id}`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
    - `Accept: application/json`

- **Response:**
  - `200 OK`
  * Example:
     ```json
                {
                    "id": 1,
                    "firstName": "admin",
                    "lastName": "adminek",
                    "email": "admin@wp.pl",
                    "role": "ROLE_ADMIN",
                    "tasks": []
                }
    ```

### Get Single User

- **URL:** `/api/users/{email}`
- **Method:** `GET`
- **Authentication** Required: Tasks retrieval is allowed without login
- **Headers:**
    - `Accept: application/json`

- **Response:**
  - `200 OK`
  * Example:
     ```json
                {
                    "id": 1,
                    "firstName": "admin",
                    "lastName": "adminek",
                    "email": "admin@wp.pl",
                    "role": "ROLE_ADMIN",
                    "tasks": []
                }
    ``` 

### Update User

- **URL:** `/api/users`
- **Method:** `PATCH`
- **Authentication** Admin or User role required
- **Headers:**
    - `Accept: application/json`
    - `Authorization: Bearer <JWT_TOKEN>`

- **Request:**
  * Example:
    ```json
        {
            "firstName": "Glen",
            "lastName": "Kutch",
            "email": "admin@wp.pl",
            "taskIds": [1]
        }
    ```

- **Response:**
  - `200 OK`
  * Example:
    ```json
      {
          "id": 1,
          "firstName": "Glen",
          "lastName": "Kutch",
          "email": "admin@wp.pl",
          "role": "ROLE_ADMIN",
          "tasks": [
                      {
                          "id": 1,
                          "title": "title_1",
                          "description": "description_1",
                          "taskStatus": "TO_DO",
                          "deadline": "2024-11-01"
                      }
                    ]
      }
    ```

### Change Password (User)

- **URL:** `/api/users/change-password`
- **Method:** `PATCH`
- **Authentication** User role required
- **Headers:**
    - `Accept: application/json`
    - `Authorization: Bearer <JWT_TOKEN>`
  
- **Request:**
  * Example:
    ```json
       {
            "oldPassword": "oldPassword123!",
            "newPassword": "NewPassword456!"
        }
    ```

- **Response:**
  - `200 OK`
  * Example:
    ```json
          "Password changed successfully. New password: NewPassword456"
    ```

### Change Password (ADMIN)

- **URL:** `/api/users/change-password/admin`
- **Method:** `PATCH`
- **Authentication** Admin role required
- **Headers:**
    - `Accept: application/json`
    - `Authorization: Bearer <JWT_TOKEN>`

- **Request:**
  * Example:
    ```json
       {
          "email": "user@example.com",
          "newPassword": "AdminPassword789!"
        }
    ```

- **Response:**
  - `200 OK`
  * Example:
    ```json
          "Password changed successfully. New password: AdminPassword789!"
    ```

### Delete User

- **URL:** `/api/users/{id}`
- **Method:** `DELETE`
- **Authentication** Admin role required
- **Headers:**
    - `Accept: application/json`
    - `Authorization: Bearer <JWT_TOKEN>`

- **Response:**
    - `200 OK`
    * Example:
      ```json
            "User removed successfully"
      ```
      
## Testing

### Unit and Integration Tests

 - Run the tests using:
   ```bash
      ./gradlew test 
    ```
Tests use an in-memory H2 database and initialize data using Liquibase.

### Postman Collections

- Postman collections for testing are included in the project:
  - Complete API Collection: /postman/All_Available_Endpoints.postman_collection.json
  - Specific Test Flow: /postman/Simply_Api_Test.postman_collection.json
Import these collections into Postman for easy testing.

## CI/CD with GitHub Actions

The project uses GitHub Actions for CI/CD. Upon every push or pull request, the pipeline:

  1. Builds the application.
  2. Runs tests.
  3. Builds and pushes the Docker image to a private repository.

For configuration, see .github/workflows/docker-ci.yml.

## Built With
- **[Java 21](https://openjdk.org/projects/jdk/21/)** - The programming language used for building the application.
- **[Spring Boot 3](https://spring.io/projects/spring-boot/)** - The framework for building Java-based web applications.
- **[Spring Security](https://spring.io/projects/spring-security)** - Framework for securing the application with authentication and authorization (JWT-based).
- **[Gradle](https://gradle.org/)** - - Dependency management and build tool for the project.
- **[MySQL](https://dev.mysql.com/doc/)** - Relational database management system used for data persistence.
- **[Docker](https://docs.docker.com/)** - Used for containerization and easy deployment via Docker Compose.
- **[Liquibase](https://www.liquibase.com/supported-databases)** - A database change management tool used for data initialization.
- **[JUnit](https://junit.org/junit5/)** - Framework for unit testing the application (including controllers, services, and repositories).
- **[Postman](https://learning.postman.com/docs/introduction/overview/?deviceId=b9b98f62-6aff-4cce-8cd9-2aa36045bd2b)** - API testing tool used for testing the REST endpoints.
- **[Lombok](https://projectlombok.org/contributing/)** - A library that reduces boilerplate code (e.g., generating getters, setters, constructors).
- **[JWT](https://jwt.io/introduction)** - (JSON Web Tokens) - Token-based authentication and authorization system.
- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)** - Used for database interaction and ORM (Object-Relational Mapping).
- **[Spring Web](https://spring.io/projects/spring-boot/)** - Used for building RESTful web services in Spring Boot.
- **[Spring Boot Actuator](https://spring.io/projects/spring-boot#actuator)** - Used for application monitoring and managing application health.
- **[H2 Database](https://www.h2database.com/html/quickstart.html)** - In-memory database used for testing purposes during unit tests.

## Authors
* zuku0404 - Initial work
