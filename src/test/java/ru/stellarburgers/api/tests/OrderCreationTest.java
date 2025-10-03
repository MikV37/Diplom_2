package ru.stellarburgers.api.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.api.BaseTest;
import ru.stellarburgers.api.client.OrderClient;
import ru.stellarburgers.api.client.UserClient;
import ru.stellarburgers.api.data.Order;
import ru.stellarburgers.api.data.User;
import ru.stellarburgers.api.data.UserGenerator;

import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTest extends BaseTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private final List<String> validIngredients = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        User user = UserGenerator.createRandomUser();
        accessToken = userClient.createUser(user).extract().path("accessToken");
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа авторизованным пользователем с валидным списком ингредиентов.")
    public void createOrderWithAuthAndIngredientsSucceeds() {
        Order order = new Order(validIngredients);
        ValidatableResponse response = orderClient.createOrder(order, accessToken);
        response.statusCode(SC_OK).body("success", equalTo(true)).body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка, что неавторизованный пользователь не может создать заказ. Тест должен падать, так как в API есть баг.")
    public void createOrderWithoutAuthFails() {
        Order order = new Order(validIngredients);
        ValidatableResponse response = orderClient.createOrder(order, null);
        response.statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка, что заказ без ингредиентов не создается. Ожидается ошибка 400.")
    public void createOrderWithoutIngredientsFails() {
        Order order = new Order(Collections.emptyList());
        ValidatableResponse response = orderClient.createOrder(order, accessToken);
        response.statusCode(SC_BAD_REQUEST).body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка, что заказ с невалидным хешем ингредиента не создается. Ожидается ошибка 500.")
    public void createOrderWithInvalidHashFails() {
        Order order = new Order(List.of("invalid_hash_made_of_fail"));
        ValidatableResponse response = orderClient.createOrder(order, accessToken);
        response.statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}