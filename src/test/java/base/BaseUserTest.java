package base;

import model.User;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;

import static data.UserGenerator.randomUser;
import static steps.UserSteps.*;

/*  Логика пользователей
    - В Before по умолчанию создаётся валидный пользователь, токен запоминается для очистки
    - Тесты, где создание - предмет проверки, переопределяют setUpUser() пустым
    - After удаляет пользователя по сохранённому accessToken
*/

/**
 * ____⚠️⚠️⚠️____ЗАМЕНИТЬ extends "BaseWireMockTest" на "BaseApiTest" при прогоне НА РЕАЛЬНОМ СЕРВЕРЕ____⚠️⚠️⚠️_______
 */
public class BaseUserTest extends BaseWireMockTest {

    protected User user;
    protected String accessToken;

    @Before
    @Step("Создание тестового пользователя")
    public void setUpUser() {
        // По умолчанию создаём валидного пользователя для каждого теста
        user = randomUser();
        Response response = createUser(user);
        rememberTokenIfCreated(response);
    }

    @Step("Запоминаем accessToken, если пользователь создан")
    protected void rememberTokenIfCreated(Response response) {
        if (response.statusCode() == 200) {
            accessToken = response.path("accessToken");
        }
    }

    @After
    @Step("Удаление тестового пользователя")
    public void tearDown() {
        if (accessToken != null) {
            Response response = deleteUser(accessToken);
            if (response.statusCode() != 202) {
                Allure.addAttachment("Ошибка очистки",
                        "Не удалось удалить пользователя, статус: " + response.statusCode());
            }
        }
        accessToken = null;
    }
}
