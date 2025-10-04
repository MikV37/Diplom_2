package ru.stellarburgers.api.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.stellarburgers.api.constants.Endpoints;
import ru.stellarburgers.api.data.User;
import static io.restassured.RestAssured.given;

public class UserClient {

    @Step("Создание пользователя {user.email}")
    public ValidatableResponse createUser(User user) {
        return given().header("Content-type", "application/json").body(user)
                .when().post(Endpoints.REGISTER_PATH).then();
    }

    @Step("Логин пользователя {user.email}")
    public ValidatableResponse loginUser(User user) {
        return given().header("Content-type", "application/json").body(user)
                .when().post(Endpoints.LOGIN_PATH).then();
    }

    @Step("Удаление пользователя по токену")
    public void deleteUser(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            given().header("Authorization", accessToken).when().delete(Endpoints.USER_PATH);
        }
    }
}