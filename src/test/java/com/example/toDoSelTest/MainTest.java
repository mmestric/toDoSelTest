package com.example.toDoSelTest;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class MainTest {
    private final MainPage mainPage = new MainPage();
    private final EditPage editPage = new EditPage();
    private final OlderPage olderPage = new OlderPage();
    private static final String TITLE_TEST ="Title Test update";
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
     *
     * The test will clear the current title and replace it with the one defined in TITLE_TEST
     * Possible to test the negative scenario, replace title with the 1501 char long one from the test documentation
     *
     * Additionally will break if title not saved or saved correctly
     */
    @Test
    public void titleChange() {
        mainPage.editButton.click();

        $(byName("title")).clear();
        $(byName("title")).sendKeys(TITLE_TEST);


        editPage.editSaveButton.click();
        String headlineText = $(byId("headline")).getText();
        //System.out.println(headlineText.equals(TITLE_TEST +" Edit"));

        //Final test on the title on the main page, should have TEXT used since the headline element will also contain the Edit text
        $(byId("headline")).shouldHave(text(TITLE_TEST));
    }


    /**
     * Test adds a new task to the top of the list with the task name defined in TASK_TEST
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


        //check if the target exists, attempting to click non existing will break
        if ($(byXpath("//*[contains(@title,'" + todayTarget + "')]")).exists())
            System.out.println("postoji element ");
        else {
            System.out.println("ne postoji element");
            $(byXpath("//*[contains(@title,'" + todayTarget + "')]")).click();
        }

        //back to main page
        olderPage.backButton.click();

        //cleanup and uncheck the task
        $(byId("today1")).click();
    }
}
