package steps;

import model.User;
import static data.Endpoints.*;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

//__Шаги проверок по созданию, логину и удалению пользователя

public class UserSteps {

    @Step("Создание пользователя: {user.email}")
    public static Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(CREATE_USER);
    }

    @Step("Логин пользователя: {user.email}")
    public static Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(LOGIN_USER);
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .delete(DELETE_USER);
    }

    @Step("Проверка accessToken: присутствует и начинается с Bearer")
    public static void checkAccessToken(Response response) {
        response
                .then()
                .body("accessToken", not(emptyOrNullString()))
                .body("accessToken", startsWith("Bearer "));
    }

    @Step("Проверка, что refreshToken не пуст")
    public static void checkRefreshToken(Response response) {
        response
                .then()
                .body("refreshToken", not(emptyOrNullString()));
    }

    @Step("Проверка данных пользователя в ответе: email и name совпадают с отправленными")
    public static void checkUserInResponse(Response response, User user) {
        response.then()
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }
}
