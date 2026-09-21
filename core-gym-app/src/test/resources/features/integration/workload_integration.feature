Feature: CRM to Workload Service Integration via RabbitMQ

  Scenario: Creating a training sends ADD workload message to queue
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Martin.Brown" trainer "Anna.Lee" name "Learning Java" type "JAVA" date "2026-11-07T18:00:00" duration 90
    Then the response status is 200
    And a workload ADD message was sent for trainer "Anna.Lee" with duration 90

  Scenario: Creating training with non-existent trainer does not send workload message
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Martin.Brown" trainer "Ghost.Trainer" name "Learning Java" type "JAVA" date "2026-11-09T18:00:00" duration 60
    Then the response status is 404
    And no workload message was sent

  Scenario: Creating multiple trainings sends multiple ADD messages
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Creating a training with trainee "Martin.Brown" trainer "Anna.Lee" name "Learning Java" type "JAVA" date "2026-11-15T18:00:00" duration 60
    And Creating a training with trainee "Martin.Brown" trainer "Anna.Lee" name "Practice Java" type "JAVA" date "2026-11-17T18:00:00" duration 30
    Then 2 workload ADD messages were sent for trainer "Anna.Lee"