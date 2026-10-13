package tests;

import base.BaseUserTest;
import model.User;
import static steps.CommonSteps.*;
import static steps.UserSteps.*;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;


@Feature("Логин пользователя")
public class UserLoginTest extends BaseUserTest {

    @Test
    @DisplayName("Логин существующего пользователя")
    @Description("Проверяем: \n" +
            "  - можно войти под существующим пользователем, \n" +
            "  - запрос возвращает правильный код ответа, \n" +
            "  - успешный запрос возвращает success: true и оба токена, \n" +
            "  - в ответе возвращаются данные созданного пользователя")
    public void testLoginExistingUser() {

        Response response = loginUser(user);

        checkStatusCode(response, 200);
        checkSuccessField(response, true);
        checkAccessToken(response);
        checkRefreshToken(response);
        checkUserInResponse(response, user);
    }

    @Test
    @DisplayName("Попытка входа с неправильным email и паролем")
    @Description("Проверяем: \n" +
            "  - нельзя войти, если неверны и email, и пароль, \n" +
            "  - запрос возвращает код 401 Unauthorized, \n" +
            "  - в ответе success: false и сообщение о неверных учётных данных")
    public void testLoginWithWrongEmailAndPassword() {

        User wrongUser = new User(
                user.getEmail() + "12345",
                "WrongPass!999",
                user.getName());

        Response response = loginUser(wrongUser);

        checkStatusCode(response, 401);
        checkSuccessField(response, false);
        checkErrorMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Попытка входа с неправильным email")
    @Description("Проверяем: \n" +
            "  - нельзя войти под несуществующим email, \n" +
            "  - запрос возвращает код 401 Unauthorized, \n" +
            "  - в ответе success: false и сообщение о неверных учётных данных")
    public void testLoginWithWrongEmail() {

        User wrongEmailUser = new User(
                user.getEmail() + "12345",
                user.getPassword(),
                user.getName());

        Response response = loginUser(wrongEmailUser);

        checkStatusCode(response, 401);
        checkSuccessField(response, false);
        checkErrorMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Попытка входа с неправильным паролем")
    @Description("Проверяем: \n" +
            "  - нельзя войти с неверным password, \n" +
            "  - запрос возвращает код 401 Unauthorized, \n" +
            "  - в ответе success: false и сообщение о неверных учётных данных")
    public void testLoginWithWrongPassword() {

        User wrongPassUser = new User(
                user.getEmail(),
                "WrongPass!999",
                user.getName());

        Response response = loginUser(wrongPassUser);

        checkStatusCode(response, 401);
        checkSuccessField(response, false);
        checkErrorMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Вход с паролем, отличающимся только регистром")
    @Description("Проверяем: \n" +
            "  - пароль в другом регистре не считается верным (сервер не нормализует пароли), \n" +
            "  - запрос возвращает код 401 Unauthorized, \n" +
            "  - в ответе success: false и сообщение о неверных учётных данных. \n" +
            "  Примечание: регистр пароля в документации не оговорён, кейс фиксирует фактическое поведение.")
    public void testLoginWithCaseChangedPassword() {

        User wrongCaseUser = new User(
                user.getEmail(),
                user.getPassword().toLowerCase(),
                user.getName());

        Response response = loginUser(wrongCaseUser);

        checkStatusCode(response, 401);
        checkSuccessField(response, false);
        checkErrorMessage(response, "email or password are incorrect");
    }
}
