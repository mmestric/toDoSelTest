package com.example.toDoSelTest;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class EditPage {

    public SelenideElement editSaveButton = $x("//input[@value='Save tasks']");
    public SelenideElement taskArea = $(byName("tasks"));
}
