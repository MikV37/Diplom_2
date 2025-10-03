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
import static org.hamcrest.Matchers.equalTo;


public class UserCreationTest extends BaseTest {

    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка, что API позволяет создать уникального пользователя с валидными данными.")
    public void createUserSuccessfully() {
        User user = UserGenerator.createRandomUser();
        ValidatableResponse response = userClient.createUser(user);
        response.statusCode(200).body("success", equalTo(true));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка, что нельзя создать пользователя с email, который уже используется. Ожидается ошибка 403.")
    public void createAlreadyRegisteredUserFails() {
        User user = UserGenerator.createRandomUser();
        accessToken = userClient.createUser(user).extract().path("accessToken");

        ValidatableResponse response = userClient.createUser(user);
        response.statusCode(403).body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }


    @Test
    @DisplayName("Создание пользователя без поля 'email'")
    @Description("Проверка, что нельзя создать пользователя без email. Ожидается ошибка 403.")
    public void createUserWithoutEmailFails() {
        User user = UserGenerator.createUserWithoutEmail();
        ValidatableResponse response = userClient.createUser(user);
        response.statusCode(403).body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без поля 'password'")
    @Description("Проверка, что нельзя создать пользователя без пароля. Ожидается ошибка 403.")
    public void createUserWithoutPasswordFails() {
        User user = UserGenerator.createUserWithoutPassword();
        ValidatableResponse response = userClient.createUser(user);
        response.statusCode(403).body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без поля 'name'")
    @Description("Проверка, что нельзя создать пользователя без имени. Ожидается ошибка 403.")
    public void createUserWithoutNameFails() {
        User user = UserGenerator.createUserWithoutName();
        ValidatableResponse response = userClient.createUser(user);
        response.statusCode(403).body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}