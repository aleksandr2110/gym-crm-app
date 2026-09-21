Feature: Training Management

  Scenario: Get all training types
    When Getting "/api/v1/trainings/types"
    Then the response status is 200
    And the response is a JSON array

  Scenario: Get all training types is publicly accessible
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Getting "/api/v1/trainings/types"
    Then the response status is 200

  Scenario: Successfully create a training session
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Martin.Brown" trainer "Anna.Lee" name "Learning Java" type "JAVA" date "2026-10-01T18:00:00" duration 60
    Then the response status is 200

  Scenario: Create training with non-existent trainee returns 404
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Invalid.Trainee" trainer "Anna.Lee" name "Learning Java" type "JAVA" date "2026-10-03T18:00:00" duration 60
    Then the response status is 404

  Scenario: Create training with non-existent trainer returns 404
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Alice.Brown" trainer "Invalid.Trainer" name "Learning Java" type "JAVA" date "2026-10-05T18:00:00" duration 60
    Then the response status is 404

  Scenario: Create training with missing training name returns 400
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Alice.Brown" trainer "Anna.Lee" name "" type "JAVA" date "2026-10-07T18:00:00" duration 60
    Then the response status is 400

  Scenario: Create training with invalid training type returns 404
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Alice.Brown" trainer "Anna.Lee" name "Learning Java" type "JAVAS" date "2026-10-09T18:00:00" duration 60
    Then the response status is 404

  Scenario: Get trainee trainings with authentication
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Getting a trainee trainings for username "Martin.Brown" trainer "Anna.Lee" period from "2026-09-01T18:00:00" to "2026-11-01T18:00:00" specialization "JAVA"
    Then the response status is 200
    And the response is a JSON array

  Scenario: Get trainee trainings without authentication returns 403
    When Getting a trainee trainings for username "Martin.Brown" trainer "Anna.Lee" period from "2026-09-01T18:00:00" to "2026-11-01T18:00:00" specialization "JAVA"
    Then the response status is 403

  Scenario: Get trainer trainings with authentication
    Given Authentication as "Anna.Lee" with role "TRAINER"
    When Getting a trainer trainings for username "Anna.Lee" trainee "Martin.Brown" period from "2026-09-01T18:00:00" to "2026-11-01T18:00:00"
    Then the response status is 200
    And the response is a JSON array