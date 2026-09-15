Feature: Authentication

  Scenario: Successful login with valid trainee credentials
    When login with username "Martin.Brown" and password "qwe"
    Then the response status is 200
    And the response contains a JWT token

  Scenario: Successful login with valid trainer credentials
    When login with username "Anna.Lee" and password "qwe"
    Then the response status is 200
    And the response contains a JWT token

  Scenario: Login fails with wrong password
    When login with username "Martin.Brown" and password "wrongPassword"
    Then the response status is 401

  Scenario: Login fails for non-existent user
    When login with username "Ghost.User" and password "qwe"
    Then the response status is 401

  Scenario: Login fails with empty username
    When login with username "" and password "qwe"
    Then the response status is 400