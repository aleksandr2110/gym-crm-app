Feature: Trainer Management

  Scenario: Successfully registration a new trainer
    When Registration a trainer with firstName "Sara" lastName "Konor" specialization "React"
    Then the response status is 201
    And the response contains a username "Sara.Konor"
    And the response contains a generated password

  Scenario: Registration a trainer with missing first name returns 400
    When Registration a trainer with firstName "" lastName "Ignat" specialization "React"
    Then the response status is 400

  Scenario: Register trainer with missing last name returns 400
    When Registration a trainer with firstName "Sergey" lastName "" specialization "React"
    Then the response status is 400

  Scenario: Register trainer with missing specialization returns 400
    When Registration a trainer with firstName "Semen" lastName "Procenko" specialization ""
    Then the response status is 400

  Scenario: Get trainer profile when authenticated
    Given Authentication as "Anna.Lee" with role "TRAINER"
    When Getting "/api/v1/trainers/Anna.Lee"
    Then the response status is 200
    And the response contains field "username" with value "Anna.Lee"
    And the response contains field "firstName" with value "Anna"
    And the response contains field "lastName" with value "Lee"

  Scenario: Get trainer profile without authentication returns 403
    When Getting "/api/v1/trainers/Anna.Lee"
    Then the response status is 403

  Scenario: Activate a trainer
    Given Authentication as "Anna.Lee" with role "ADMIN"
    When Using patch end-point "/api/v1/trainers/activation" with param "username" "Juli.Huston" and param "isActive" "true"
    Then the response status is 200

  Scenario: Deactivate a trainer
    Given Authentication as "Anna.Lee" with role "ADMIN"
    When Using patch end-point "/api/v1/trainers/activation" with param "username" "Anna.Lee" and param "isActive" "false"
    Then the response status is 200

  Scenario: Activate non-existent trainer returns 404
    Given Authentication as "Anna.Lee" with role "ADMIN"
    When Using patch end-point "/api/v1/trainers/activation" with param "username" "Ghost.Trainer" and param "isActive" "true"
    Then the response status is 404