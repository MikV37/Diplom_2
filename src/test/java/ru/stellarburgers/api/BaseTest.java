package ru.stellarburgers.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class BaseTest {

    @BeforeClass
    public static void setUpAll() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        RestAssured.filters(new AllureRestAssured());
    }
}