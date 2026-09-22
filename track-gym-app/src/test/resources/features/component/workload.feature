Feature: Trainer Workload Management

  Scenario: Successfully add training workload for a new trainer
    Given No workload exists for trainer "Anna.Lee"
    When Send POST for a workload request for trainer "Anna.Lee" firstName "Anna" lastName "Lee" action "ADD" duration 60 date "2026-11-07T18:00:00"
    Then The response status is 200
    And The workload for trainer "Anna.Lee" in year 2026 month 11 is 60 minutes

  Scenario: Successfully add workload for existing trainer accumulates duration
    Given A workload exists for trainer "Anna.Lee" with 60 minutes in year 2026 month 11
    When Send POST for a workload request for trainer "Anna.Lee" firstName "Anna" lastName "Lee" action "ADD" duration 30 date "2026-11-08T18:00:00"
    Then The response status is 200
    And The workload for trainer "Anna.Lee" in year 2026 month 11 is 90 minutes

  Scenario: Successfully delete training from workload
    Given A workload exists for trainer "Anna.Lee" with 90 minutes in year 2026 month 11
    When Send POST for a workload request for trainer "Anna.Lee" firstName "Anna" lastName "Lee" action "DELETE" duration 90 date "2026-11-08T18:00:00"
    Then The response status is 200
    And The workload for trainer "Anna.Lee" in year 2026 month 11 is 0 minutes

  Scenario: Add workload with missing username returns 400
    When Send post a workload request with missing username
    Then The response status is 400

  Scenario: Add workload with missing first name returns 400
    When Send post a workload request with missing firstName
    Then The response status is 400

  Scenario: Add workload with negative duration returns 400
    When Send POST for a workload request for trainer "Jeff.Dorf" firstName "Jeff" lastName "Dorf" action "ADD" duration -10 date "2026-11-07T18:00:00"
    Then The response status is 400

  Scenario: Add workload without authentication returns 403
    When Send post an unauthenticated workload request for trainer "Jeff.Dorf"
    Then The response status is 403

  Scenario: Get workload for non-existent trainer returns 404
    Given No workload exists for trainer "Ghost.Trainer"
    When Getting workload for trainer "Ghost.Trainer"
    Then The response status is 400

  Scenario: Get all workloads without authentication returns 200
    When Getting all workloads unauthenticated
    Then The response status is 200

  Scenario: Add workload for non existing trainer
    Given No workload exists for trainer "David.Hook"
    When Send POST for a workload request for trainer "David.Hook" firstName "David" lastName "Hook" action "ADD" duration 60 date "2026-12-08T18:00:00"
    Then The response status is 200
    And The workload for trainer "David.Hook" in year 2026 month 12 is 60 minutes

  Scenario: Get all trainer workloads
    Given Workloads exist for trainers "Anna.Lee" and "David.Hook"
    When Getting all workloads
    Then The response status is 200
    And The response is a JSON array with at least 2 entries