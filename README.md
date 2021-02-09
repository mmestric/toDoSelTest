# toDoSelTest
Example tests for the second interview task

The application created for this assignment is more for demonstration purposes and demonstrates some of the basic principles in automated testing.

This includes elements from simple DOM traversal, Data Driven Testing principles (DDT) and a slightly more complicated targeting test to illustrate the issues with poorly written source software.
The tests have comments written in code to better describe some of the functionality, and a more detailed description in the accompanying document. 

The standard practice is decoupling the data from the tests them selves as demonstrated in the titleChange test.

Test class is located in test/java/com/example/toDoSelTest
Targeting classes have been created in main/java/com/example/toDoSelTest and contain classes for each of the page tested
  - usually those classes would contain all the potential targets on the pages in question 
  
The test data is in the project root (test1Title.csv) and it follows the basic principle of DDT. The data is stored in the format of {input},{expected result} pairs, with every row representing an additional test run.

Once completed the test will generate a basic allure result (allure-results) and basic test report with a screenshot in (build/reports/tests)


The following tests have been implemented: 
- titleChange: basic test demonstrating the DDT principle, reads test data from an csv file, prepares the data and runs the test for each row. As currently set up the last run will fail since it will hit the unhandled error.
- addingTask: basic traversal on the front end and setting a value set in a coded in constant, once finished the test runs basic cleanup to ensure repeatability
- checkboxTest: demonstrates potential issues with poorly written code, due to lack of simple unique identifiers, the only way to target and confirm that the status was changed is to use dates to create a string to use in matching to target a specific element. Once located soft assert is used not to break the entire test execution.
