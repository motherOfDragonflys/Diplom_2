package tests;

import base.BaseUserTest;
import static data.UserGenerator.*;
import static steps.UserSteps.*;
import static steps.CommonSteps.*;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

@Feature("Создание пользователя")
public class UserCreateTest extends BaseUserTest {

    // Before на создание отключаем, мы и так проверяем создание пользователя
    @Override
    public void setUpUser() {
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяем: \n" +
            "  - уникального пользователя можно создать, \n" +
            "  - запрос возвращает правильный код ответа, \n" +
            "  - успешный запрос возвращает success: true и оба токена, \n" +
            "  - в ответе возвращаются данные созданного пользователя")
    public void testCreateUniqueUser() {

        user = randomUser();
        Response response = createUser(user);
        rememberTokenIfCreated(response);

        checkStatusCode(response, 200);
        checkSuccessField(response, true);
        checkAccessToken(response);
        checkRefreshToken(response);
        checkUserInResponse(response, user);
    }

    @Test
    @DisplayName("Попытка создать пользователя, который уже зарегистрирован")
    @Description("Проверяем: \n" +
            "  - повторное создание пользователя с теми же данными невозможно, \n" +
            "  - запрос возвращает код 403 Forbidden, \n" +
            "  - в ответе success: false и сообщение, что пользователь уже зарегистрирован")
    public void testCreateDuplicateUser() {

        user = randomUser();

        Response creationResponse = createUser(user);
        checkStatusCode(creationResponse, 200);
        rememberTokenIfCreated(creationResponse);

        Response duplicateResponse = createUser(user);

        checkStatusCode(duplicateResponse, 403);
        checkSuccessField(duplicateResponse, false);
        checkErrorMessage(duplicateResponse, "User already exists");
    }

    @Test
    @DisplayName("Попытка создать пользователя без email")
    @Description("Проверяем: \n" +
            "  - нельзя создать пользователя без обязательного поля email, \n" +
            "  - запрос возвращает код 403 Forbidden, \n" +
            "  - в ответе success: false и сообщение об обязательности полей")
    public void testCreateUserWithoutEmail() {

        user = randomUser();
        user.setEmail(null);

        Response response = createUser(user);
        rememberTokenIfCreated(response);

        checkStatusCode(response, 403);
        checkSuccessField(response, false);
        checkErrorMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Попытка создать пользователя без пароля")
    @Description("Проверяем: \n" +
            "  - нельзя создать пользователя без обязательного поля password, \n" +
            "  - запрос возвращает код 403 Forbidden, \n" +
            "  - в ответе success: false и сообщение об обязательности полей")
    public void testCreateUserWithoutPassword() {

        user = randomUser();
        user.setPassword(null);

        Response response = createUser(user);
        rememberTokenIfCreated(response);

        checkStatusCode(response, 403);
        checkSuccessField(response, false);
        checkErrorMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Попытка создать пользователя без имени")
    @Description("Проверяем: \n" +
            "  - нельзя создать пользователя без обязательного поля name, \n" +
            "  - запрос возвращает код 403 Forbidden, \n" +
            "  - в ответе success: false и сообщение об обязательности полей")
    public void testCreateUserWithoutName() {

        user = randomUser();
        user.setName(null);

        Response response = createUser(user);
        rememberTokenIfCreated(response);

        checkStatusCode(response, 403);
        checkSuccessField(response, false);
        checkErrorMessage(response, "Email, password and name are required fields");
    }
}
