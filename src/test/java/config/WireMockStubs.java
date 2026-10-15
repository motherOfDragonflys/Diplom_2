package config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import data.Endpoints;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


/**
 *__⚠️⚠️⚠️____НЕ ИСПОЛЬЗОВАТЬ КЛАСС при прогоне НА РЕАЛЬНОМ СЕРВЕРЕ____⚠️⚠️⚠️__
 * тут живут заглушки ответов сервера
 */
public final class WireMockStubs {

    private WireMockStubs() {}

    private static final String TOKENS =
            "{\n" +
                    "  \"success\": true,\n" +
                    "  \"accessToken\": \"Bearer test-token-123\",\n" +
                    "  \"refreshToken\": \"test-refresh-123\",\n" +
                    "  \"user\": {\n" +
                    "    \"email\": \"{{jsonPath request.body '$.email'}}\",\n" +
                    "    \"name\": \"{{jsonPath request.body '$.name'}}\"\n" +
                    "  }\n" +
                    "}";

    private static final String ERROR_REQUIRED_FIELDS =
            "{\"success\": false, " +
            "\"message\": \"Email, password and name are required fields\"}";

    private static final String ERROR_CREDENTIALS =
            "{\"success\": false, " +
            "\"message\": \"email or password are incorrect\"}";

    public static void registerAll(WireMockServer server) {

//______________РЕГИСТРАЦИЯ_____________________________________________________________________________________________

        // 403: одно из обязательных полей отсутствует, null или пустое
        server.stubFor(post(Endpoints.CREATE_USER)
                .atPriority(1)
                .withRequestBody(matchingJsonPath(
                        "$[?(@.email == null || @.password == null || @.name == null"
                                + " || @.email == '' || @.password == '' || @.name == '')]"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ERROR_REQUIRED_FIELDS)));

        // 200: успешная регистрация — только первый вызов за тест
        server.stubFor(post(Endpoints.CREATE_USER)
                .atPriority(10)
                .inScenario("register")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(TOKENS)
                        .withTransformers("response-template"))
                .willSetStateTo("USER_EXISTS"));

        // 403: повторная регистрация того же пользователя
        server.stubFor(post(Endpoints.CREATE_USER)
                .atPriority(10)
                .inScenario("register")
                .whenScenarioStateIs("USER_EXISTS")
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": false, \"message\": \"User already exists\"}")));

        // ---------- Логин ----------

        // 401: email с суффиксом "12345" — так устроены негативные тесты
        server.stubFor(post(Endpoints.LOGIN_USER)
                .atPriority(1)
                .withRequestBody(matching(".*\"email\"\\s*:\\s*\"[^\"]*12345\".*"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ERROR_CREDENTIALS)));


        // 401: заведомо неверный пароль из тестов
        server.stubFor(post(Endpoints.LOGIN_USER)
                .atPriority(1)
                .withRequestBody(containing("WrongPass!999"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ERROR_CREDENTIALS)));

        // 401: пароль без заглавных букв — регистр изменён
        server.stubFor(post(Endpoints.LOGIN_USER)
                .atPriority(1)
                .withRequestBody(matchingJsonPath("$[?(@.password =~ /^[^A-Z]*$/)]"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ERROR_CREDENTIALS)));

        // 200: логин под зарегистрированного пользователя — фолбэк
        server.stubFor(post(Endpoints.LOGIN_USER)
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(TOKENS)
                        .withTransformers("response-template")));

        // ---------- Ингредиенты ----------

        server.stubFor(get(Endpoints.GET_INGREDIENTS)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": true, \"data\": [\n" +
                                "  {\"_id\": \"61c0c5a71d1f82001bdaaa6d\", \"name\": \"Краторная булка N-200i\"},\n" +
                                "  {\"_id\": \"61c0c5a71d1f82001bdaaa6e\", \"name\": \"Говяжий метеорит (отбивная)\"},\n" +
                                "  {\"_id\": \"61c0c5a71d1f82001bdaaa6f\", \"name\": \"Соус фирменный Space Sauce\"}\n" +
                                "]}")));

        // ---------- Заказ ----------

        // 401: без заголовка Authorization
        server.stubFor(post(Endpoints.CREATE_ORDER)
                .atPriority(1)
                .withHeader("Authorization", absent())
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": false, \"message\": \"You should be authorised\"}")));

        // 400: пустой массив ингредиентов
        server.stubFor(post(Endpoints.CREATE_ORDER)
                .atPriority(1)
                .withRequestBody(containing("\"ingredients\":[]"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": false, \"message\": \"Ingredient ids must be provided\"}")));

        // 400: невалидный хеш
        server.stubFor(post(Endpoints.CREATE_ORDER)
                .atPriority(1)
                .withRequestBody(containing("invalid_hash"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": false, \"message\": \"One or more ids provided are incorrect\"}")));

        // 200: остальные заказы — фолбэк
        server.stubFor(post(Endpoints.CREATE_ORDER)
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"success\": true,\n" +
                                "  \"name\": \"Экзотический месси-бургер\",\n" +
                                "  \"order\": {\n" +
                                "    \"number\": 1234\n" +
                                "  }\n" +
                                "}")));

        // ---------- Удаление пользователя ----------
        server.stubFor(delete(Endpoints.DELETE_USER)
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": true}")));
    }
}
