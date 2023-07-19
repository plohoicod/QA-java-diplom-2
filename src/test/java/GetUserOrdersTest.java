import dto.IngredientsDto;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(JUnit4.class)
public class GetUserOrdersTest {
    private final OrderClient orderClient = new OrderClient();

    private final AuthClient authClient = new AuthClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = TestData.BASE_URI;
        authClient.registerUser(new RegisterDto(TestData.EMAIL_1, TestData.PASSWORD_1, TestData.NAME_1));
        orderClient.createOrderWithToken(
                authClient.getAuthToken(TestData.EMAIL_1, TestData.PASSWORD_1),
                new IngredientsDto(List.of(TestData.INGREDIENT, TestData.INGREDIENT2)));
    }

    @After
    public void clearData() {
        authClient.deleteUser(TestData.EMAIL_1, TestData.PASSWORD_1);
    }

    @Test
    @DisplayName("Проверка успешного получения листа заказов пользователя")
    @Description("Проверка возврата статуса 200 и тела ответа")
    public void getUserOrderListSuccess() {
        orderClient.getUserOrdersWithToken(authClient.getAuthToken(TestData.EMAIL_1, TestData.PASSWORD_1))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("orders.size()", equalTo(1))
                .and()
                .body("orders[0].ingredients.size()", equalTo(2))
                .and()
                .body("orders[0].ingredients[0]", equalTo(TestData.INGREDIENT))
                .and()
                .body("orders[0].ingredients[1]", equalTo(TestData.INGREDIENT2));
    }

    @Test
    @DisplayName("Проверка ошибки получения списка заказов пользователя без авторизации")
    @Description("Проверка возврата статуса 401 и тела ответа")
    public void etUserOrderListWithoutAuthorisationFail() {
        orderClient.getUserOrdersWithoutToken()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));

    }
}
