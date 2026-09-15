Feature: Trainee Management

  Scenario: Successfully register a new trainee
    When Registration a trainee with firstName "Igor" lastName "Petrenko" dateOfBirth "1988-09-11" address "Dnipro Xmelnickogo 32 st."
    Then the response status is 201
    And the response contains a username "Igor.Petrenko"
    And the response contains a generated password

  Scenario: Register trainee with missing first name returns 400
    When Registration a trainee with firstName "" lastName "Petrenko" dateOfBirth "1988-09-01" address "Dnipro Xmelnickogo 2 st."
    Then the response status is 400

  Scenario: Register trainee with missing last name returns 400
    When Registration a trainee with firstName "Ivan" lastName "" dateOfBirth "1988-01-01" address "Dnipro Xmelnickogo 32 st."
    Then the response status is 400

  Scenario: Get trainee profile when authenticated
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Getting "/api/v1/trainees/Martin.Brown"
    Then the response status is 200
    And the response contains field "username" with value "Martin.Brown"
    And the response contains field "firstName" with value "Martin"
    And the response contains field "lastName" with value "Brown"

  Scenario: Get trainee profile without authentication returns 403
    When Getting "/api/v1/trainees/Martin.Brown"
    Then the response status is 403

  Scenario: Activate a trainee
    Given Authentication as "Martin.Brown" with role "ADMIN"
    When Using patch end-point "/api/v1/trainees/activation" with param "username" "Bob.Wilson" and param "isActive" "true"
    Then the response status is 200

  Scenario: Deactivate a trainee
    Given Authentication as "Martin.Brown" with role "ADMIN"
    When Using patch end-point "/api/v1/trainees/activation" with param "username" "Martin.Brown" and param "isActive" "false"
    Then the response status is 200

  Scenario: Activate non-existent trainee returns 404
    Given Authentication as "Martin.Brown" with role "ADMIN"
    When Using patch end-point "/api/v1/trainees/activation" with param "username" "Invalid.User" and param "isActive" "true"
    Then the response status is 404

  Scenario: Delete trainee by admin
    Given Authentication as "Anna.Lee" with role "ADMIN"
    When Deleting "/api/v1/trainees/David.Davis"
    Then the response status is 204

  Scenario: Delete non-existent trainee returns 404
    Given Authentication as "Anna.Lee" with role "ADMIN"
    When Deleting "/api/v1/trainees/NonExistent.User"
    Then the response status is 404

  Scenario: Update trainee trainers list
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Updating trainee "Martin.Brown" trainers list with trainer usernames "Anna.Lee,Juli.Huston"
    Then the response status is 200
    And the response is a JSON array

  Scenario: Get available trainers for authenticated trainee
    Given Authentication as "Martin.Brown" with role "TRAINEE"
    When Getting "/api/v1/trainees/Martin.Brown/available-trainers"
    Then the response status is 200
    And the response is a JSON array