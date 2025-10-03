package ru.stellarburgers.api.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import ru.stellarburgers.api.constants.Endpoints;
import ru.stellarburgers.api.data.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String accessToken) {

        RequestSpecification request = given()
                .header("Content-type", "application/json")
                .body(order);

        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }

        return request.when()
                .post(Endpoints.ORDERS_PATH)
                .then();
    }
}