import dto.RegisterDto;
import dto.UserInfoDto;
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

@RunWith(JUnit4.class)
public class ChangeUserDataTest {

    private final AuthClient authClient = new AuthClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = TestData.BASE_URI;
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, TestData.PASSWORD_1, TestData.NAME_1));
    }

    @After
    public void clearData() {
        authClient.deleteUser(TestData.EMAIL_1, TestData.PASSWORD_1);
        authClient.deleteUser(TestData.EMAIL_3, TestData.PASSWORD_1);
        authClient.deleteUser(TestData.EMAIL_2, TestData.PASSWORD_2);
    }

    @Test
    @DisplayName("Проверка успешного редактирования данных пользователя")
    @Description("Проверка возврата статуса 200 и тела ответа")
    public void changeUserCredsSuccess() {
        authClient.patchUserInfoWithToken(authClient.getAuthToken(TestData.EMAIL_1, TestData.PASSWORD_1),
                new UserInfoDto(TestData.EMAIL_3, TestData.NAME_2))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(TestData.EMAIL_3))
                .and()
                .body("user.name", equalTo(TestData.NAME_2));

    }

    @Test
    @DisplayName("Проверка ошибки редактирования пользователя без авторизации")
    @Description("Проверка возврата статуса 401 и тела ответа")
    public void changeUserCredsWithoutAuthorisationFail() {
        authClient.patchUserInfoWithoutToken(new UserInfoDto(TestData.EMAIL_3, TestData.NAME_2))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));

    }

    @Test
    @DisplayName("Проверка ошибки редактирования пользователя c почтой, которая уже принадлежит другому пользователю")
    @Description("Проверка возврата статуса 403 и тела ответа")
    public void changeUserCredsWithUsedEmailFail() {
        authClient.registerUser(new RegisterDto(TestData.EMAIL_2, TestData.PASSWORD_2, TestData.NAME_2));
        authClient.patchUserInfoWithToken(authClient.getAuthToken(TestData.EMAIL_1, TestData.PASSWORD_1),
                        new UserInfoDto(TestData.EMAIL_2, TestData.NAME_2))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User with such email already exists"));

    }


}
