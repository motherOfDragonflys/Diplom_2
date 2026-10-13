package steps;

import model.Order;
import static data.Endpoints.*;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

public class OrderSteps {

    @Step("Получение списка ингредиентов")
    public static Response getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .get(GET_INGREDIENTS);
    }

    @Step("Получение первых {count} хешей ингредиентов")
    public static String[] getIngredientHashes(int count) {
        List<String> hashes = getIngredients()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data._id");
        return hashes.subList(0, count).toArray(new String[0]);
    }

    @Step("Создание заказа с авторизацией")
    public static Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .post(CREATE_ORDER);
    }

    @Step("Создание заказа без авторизации")
    public static Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(CREATE_ORDER);
    }

    @Step("Проверка: номер заказа — положительное число")
    public static void checkOrderNumber(Response response) {
        response
                .then()
                .body("order.number", greaterThan(0));
    }

    @Step("Проверка: в ответе есть имя бургера")
    public static void checkOrderName(Response response) {
        response
                .then()
                .body("name", notNullValue());
    }

}
