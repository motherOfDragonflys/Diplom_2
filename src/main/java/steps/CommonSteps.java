package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

//__Общие шаги с проверкой ответа

public class CommonSteps {

    @Step("Проверка кода ответа: ожидается {expected}")
    public static void checkStatusCode(Response response, int expected) {
        response
                .then()
                .statusCode(expected);
    }

    @Step("Проверка success = {expected}")
    public static void checkSuccessField(Response response, boolean expected) {
        response
                .then()
                .body("success", is(expected));
    }

    @Step("Проверка текста ошибки")
    public static void checkErrorMessage(Response response, String expected) {
        response
                .then()
                .body("message", equalTo(expected));
    }
}
