package tests;

import base.BaseUserTest;

import io.qameta.allure.Issue;
import model.Order;

import static org.apache.http.HttpStatus.*;
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
    @DisplayName("Создание заказа с авторизацией, с валидными ингредиентами")
    @Description("Проверяем: \n" +
            "  - можно создать заказ под авторизованным пользователем, \n" +
            "  - запрос возвращает код 200 OK, \n" +
            "  - в ответе success: true, имя бургера и номер заказа")
    public void testCreateOrderWithAuth() {

        Response response = createOrder(
                new Order(getIngredientHashes(3)),
                accessToken
        );

        checkStatusCode(response, SC_OK);
        checkSuccessField(response, true);
        checkOrderName(response);
        checkOrderNumber(response);
    }

    @Test
    @DisplayName("Попытка создать заказ без авторизации, с валидными ингредиентами")
    @Description("Проверяем: \n" +
            "  - нельзя создать заказ без авторизации, \n" +
            "  - запрос возвращает код 401 Unauthorized, \n" +
            "  - в ответе success: false")
    @Issue("BUG-1 Заказ создается без авторизации: ответ 200 вместо 401")
    public void testCreateOrderWithoutAuth() {

        Response response = createOrderWithoutAuth(
                new Order(getIngredientHashes(3))
        );

        checkStatusCode(response, SC_UNAUTHORIZED);
        checkSuccessField(response, false);
    }

    @Test
    @DisplayName("Попытка создать заказ с авторизацией, без ингредиентов")
    @Description("Проверяем: \n" +
            "  - нельзя создать заказ без ингредиентов, \n" +
            "  - запрос возвращает код 400 Bad Request, \n" +
            "  - в ответе success: false и сообщение об обязательности ингредиентов")
    public void testCreateOrderWithoutIngredients() {

        Response response = createOrder(
                new Order(new String[0]),
                accessToken
        );

        checkStatusCode(response, SC_BAD_REQUEST);
        checkSuccessField(response, false);
        checkErrorMessage(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Попытка создать заказ с авторизацией и неверным хешем ингредиента")
    @Description("Проверяем: \n" +
            "  - заказ с несуществующим хешем ингредиента не создаётся, \n" +
            "  - сервер возвращает код 400 Bad Request.\n" +
            "Примечание: в документации для этого кейса указан 500 Internal Server Error, ожидаем 400 по уточнению.")
    @Issue("BUG-2 Ошибка 500 \"Internal Server Error\" вместо 400")
    public void testCreateOrderWithInvalidHash() {

        String[] hashes = getIngredientHashes(3);
        hashes[1] = "invalid_hash_12345";

        Response response = createOrder(
                new Order(hashes),
                accessToken
        );

        checkStatusCode(response, SC_BAD_REQUEST);
        checkSuccessField(response, false);
    }
}
