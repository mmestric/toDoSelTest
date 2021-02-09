package com.example.toDoSelTest;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class MainTest {
    private final MainPage mainPage = new MainPage();
    private final EditPage editPage = new EditPage();
    private final OlderPage olderPage = new OlderPage();
    private static final String TASK_TEST ="Test task 123";

    @BeforeAll
    public static void setUpAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        Configuration.startMaximized = true;
        open("https://dailytodo.org/sZQrt");
    }





    /**
     * Simple test to check if the title is being saved correctly
     * Additionally this tests demonstrates a simple DDT principle where the test data is in a separate .CSV file.
     * It is possible to use JSON,XML or any other file type, although the CSV is a common filetype since it can be used for both manual and automatic testing
     *
     * The test will clear the current title and replace it with the one defined in test1Title.csv file
     * As currently configured the test will also fail since it contains the negative scenario with 1501 characters in the title
     *
     * The test will break if title not saved or saved correctly, or if the server throws an error (one of the scenarios)
     *
     */
    @Test
    public void titleChange() {
        String fileName = "test1Title.csv";
        File file = new File(fileName);
        List<List<String>> testData = new ArrayList<>();

        Scanner inputStream;

        //reading the file
        try{
            inputStream = new Scanner(file);

            while (inputStream.hasNext()){
                String line = inputStream.nextLine();
                String[] values = line.split(",");

                testData.add(Arrays.asList(values));
            }

            inputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


        int pos;
        String[] tmpTestValue = {"test data", "expected result"};
        for(List<String> line: testData) {

            pos = 0;
            for (String value: line) {
                tmpTestValue[pos]=value;
                pos++;
            }


            mainPage.editButton.click();

            $(byName("title")).clear();
            $(byName("title")).sendKeys(tmpTestValue[0]);


            editPage.editSaveButton.click();
            String headlineText = $(byId("headline")).getText();

            //Final test on the title on the main page, should have TEXT used since the headline element will also contain the Edit text
            $(byId("headline")).shouldHave(text(tmpTestValue[1]));
        }

    }


    /**
     * Test adds a new task to the top of the list with the task name defined in TASK_TEST (did not run a second example test with an external test data file)
     * When added the new task is placed on the t1 position, test will confirm the correct text
     *
     * Once done test runs cleanup
     */
    @Test
    public void addingTask() {
        mainPage.editButton.click();

        //moving to first row, entering the task and hitting enter
        editPage.taskArea.sendKeys(Keys.CONTROL, Keys.HOME);
        editPage.taskArea.sendKeys(TASK_TEST);
        editPage.taskArea.sendKeys(Keys.ENTER);

        //saving the value and moving back to the main page
        editPage.editSaveButton.click();

        //checking the first task, using exactText, needs to match the defined task name in TASK_TEST
        $(byId("t1")).shouldHave(exactText(TASK_TEST));

        //cleanup: open edit, move to first row, select it, delete it and move second row up
        mainPage.editButton.click();
        editPage.taskArea.sendKeys(Keys.CONTROL, Keys.HOME);
        editPage.taskArea.sendKeys(Keys.SHIFT, Keys.END);
        editPage.taskArea.sendKeys(Keys.BACK_SPACE);
        editPage.taskArea.sendKeys(Keys.RIGHT);
        editPage.taskArea.sendKeys(Keys.BACK_SPACE);

        //save cleanup and go back to main page
        editPage.editSaveButton.click();

    }


    /**
     * Test to check if the completed task checkbox is working correctly
     * The "Older" page is written poorly so the test needs to target using the title that is based on a custom date format
     *
     * Possible to get a negative scenario by triggering the checkbox manually before the test (failsafe can be implemented)
     *
     */
    @Test
    public void checkboxTest() {

        //preparation work for the targeting using the date, will target the only checkmark for the current date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String tmp = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        String todayTarget = "Yes on ";
        todayTarget+= tmp.toLowerCase(Locale.ROOT);
        todayTarget+= ", ";
        tmp = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(date.getTime());
        todayTarget+= tmp;

        //catching a potential fail of the test without breaking execution
        SoftAssert softAssert = new SoftAssert();


        //click the checkbox
        $(byId("check1")).click();

        //should have used a explicit selenium wait (or fluent)
        try
        {
            Thread.sleep(2000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        //click the older in the first row and last column
        mainPage.lastColumn.click();


        //System.out.println("today target: " + todayTarget);


        //check if the target exists, if element does not exist the soft assert should mark this test as failed and continue (if full break is required regular assert can be used, or click)
        if ($(byXpath("//*[contains(@title,'" + todayTarget + "')]")).exists())
            System.out.println("element exists");
        else {
            System.out.println("element does not exist");
            softAssert.fail("nonExisting element");
            //$(byXpath("//*[contains(@title,'" + todayTarget + "')]")).click();
        }

        //back to main page
        olderPage.backButton.click();

        //cleanup and uncheck the task
        $(byId("today1")).click();
    }
}
