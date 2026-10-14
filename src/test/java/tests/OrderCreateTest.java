package tests;

import base.BaseUserTest;

import model.Order;
import static steps.CommonSteps.*;
import static steps.OrderSteps.*;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

@Feature("Создание заказа")
public class OrderCreateTest extends BaseUserTest {

    @Test
    @DisplayName("Заказ с авторизацией и валидными ингредиентами")
    @Description("Проверяем: \n" +
            "  - можно создать заказ под авторизованным пользователем, \n" +
            "  - запрос возвращает код 200 OK, \n" +
            "  - в ответе success: true, имя бургера и номер заказа")
    public void testCreateOrderWithAuth() {

        Response response = createOrder(
                new Order(getIngredientHashes(3)),
                accessToken
        );

        checkStatusCode(response, 200);
        checkSuccessField(response, true);
        checkOrderName(response);
        checkOrderNumber(response);
    }

    @Test
    @DisplayName("Заказ без авторизации и валидными ингредиентами")
    @Description("Проверяем: \n" +
            "  - нельзя создать заказ без авторизации, \n" +
            "  - запрос возвращает код 401 Unauthorized, \n" +
            "  - в ответе success: false")
    public void testCreateOrderWithoutAuth() {

        Response response = createOrderWithoutAuth(
                new Order(getIngredientHashes(3))
        );

        checkStatusCode(response, 401);
        checkSuccessField(response, false);
    }

    @Test
    @DisplayName("Заказ с авторизацией без ингредиентов")
    @Description("Проверяем: \n" +
            "  - нельзя создать заказ без ингредиентов, \n" +
            "  - запрос возвращает код 400 Bad Request, \n" +
            "  - в ответе success: false и сообщение об обязательности ингредиентов")
    public void testCreateOrderWithoutIngredients() {

        Response response = createOrder(
                new Order(new String[0]),
                accessToken
        );

        checkStatusCode(response, 400);
        checkSuccessField(response, false);
        checkErrorMessage(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Заказ с авторизацией и неверным хешем ингредиента")
    @Description("Проверяем: \n" +
            "  - заказ с несуществующим хешем ингредиента не создаётся, \n" +
            "  - сервер возвращает код 400 Bad Request.\n" +
            "Примечание: в документации для этого кейса указан 500 Internal Server Error, ожидаем 400 по уточнению.")
    public void testCreateOrderWithInvalidHash() {

        String[] hashes = getIngredientHashes(3);
        hashes[1] = "invalid_hash_12345";

        Response response = createOrder(
                new Order(hashes),
                accessToken
        );

        checkStatusCode(response, 400);
        checkSuccessField(response, false);
    }
}
