package ru.stellarburgers.api.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.api.BaseTest;
import ru.stellarburgers.api.client.UserClient;
import ru.stellarburgers.api.data.User;
import ru.stellarburgers.api.data.UserGenerator;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest extends BaseTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.createRandomUser();
        accessToken = userClient.createUser(user).extract().path("accessToken");
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка успешной авторизации с корректными данными. Ожидается статус-код 200.")
    public void loginWithValidCredentials() {
        ValidatableResponse response = userClient.loginUser(user);
        response.statusCode(SC_OK).body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что авторизация с неверным паролем не проходит. Ожидается ошибка 401.")
    public void loginWithInvalidPasswordFails() {
        User invalidUser = new User(user.getEmail(), "wrong_password", user.getName());
        ValidatableResponse response = userClient.loginUser(invalidUser);
        response.statusCode(SC_UNAUTHORIZED).body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным логином (email)")
    @Description("Проверка, что авторизация с неверным логином не проходит. Ожидается ошибка 401.")
    public void loginWithInvalidLoginFails() {
        // Генерируем случайный email, которого точно нет в системе
        String nonExistentEmail = UserGenerator.createRandomUser().getEmail();
        User invalidUser = new User(nonExistentEmail, user.getPassword(), user.getName());

        ValidatableResponse response = userClient.loginUser(invalidUser);
        response.statusCode(SC_UNAUTHORIZED).body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}