package com.example.toDoSelTest;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    public SelenideElement editButton = $("[href=\"?edit=1\"]");
    public SelenideElement lastColumn = $x("//tr[contains(@class, 't1 tasktr')]//td[last()]");

}
