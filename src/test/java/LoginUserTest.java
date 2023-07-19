import dto.LoginDto;
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
public class LoginUserTest {

    private final AuthClient authClient = new AuthClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = TestData.BASE_URI;
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, TestData.PASSWORD_1, TestData.NAME_1));
    }

    @After
    public void clearData() {
        authClient.deleteUser(TestData.EMAIL_1, TestData.PASSWORD_1);
    }

    @Test
    @DisplayName("Проверка успешного входа существующего пользоваателя")
    @Description("Проверка возврата статуса 200 и тела ответа")
    public void loginUserSuccess() {
        authClient.loginUser(new LoginDto(TestData.EMAIL_1, TestData.PASSWORD_1))
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
    @DisplayName("Проверка ошибки при входе несуществующего пользователя")
    @Description("Проверка возврата статуса 401 и тела ответа")
    public void loginUserWithWrongCredsFail() {
        authClient.loginUser(new LoginDto(TestData.EMAIL_2, TestData.PASSWORD_2))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}
