import dto.RegisterDto;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(JUnit4.class)
public class CreateUserTest {

    private final AuthClient authClient = new AuthClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = TestData.BASE_URI;
    }

    @After
    public void clearData() {
        authClient.deleteUser(TestData.EMAIL_1, TestData.PASSWORD_1);
    }



    @Test
    @DisplayName("Проверка успешного создания уникального пользователя")
    @Description("Проверка возврата статуса 200 и тела ответа")
    public void createUserSuccess() {
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, TestData.PASSWORD_1, TestData.NAME_1))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .and()
                .body("user", notNullValue());
    }

    @Test
    @DisplayName("Проверка ошибки создания дубликата пользователя")
    @Description("Проверка возврата статуса 403 и тела ответа")
    public void createUserDuplicateFail() {
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, TestData.PASSWORD_1, TestData.NAME_1));
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, TestData.PASSWORD_1, TestData.NAME_1))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Проверка ошибки создания пользователя без имени")
    @Description("Проверка возврата статуса 403 и тела ответа")
    public void createUserWithoutNameFail() {
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, TestData.PASSWORD_1, null))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка ошибки создания пользователя без пароля")
    @Description("Проверка возврата статуса 403 и тела ответа")
    public void createUserWithoutPasswordFail() {
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, null, TestData.NAME_1))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка ошибки создания пользователя без email")
    @Description("Проверка возврата статуса 403 и тела ответа")
    public void createUserWithoutEmailFail() {
        authClient.registerUser(new RegisterDto(null, TestData.PASSWORD_1, TestData.NAME_1))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

}
