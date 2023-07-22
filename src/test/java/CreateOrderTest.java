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
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(JUnit4.class)
public class CreateOrderTest {

    private final OrderClient orderClient = new OrderClient();

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
    @DisplayName("Проверка успешного создания заказа")
    @Description("Проверка возврата статуса 200 и тела ответа")
    public void createOrderSuccess() {

        orderClient.createOrderWithToken(
                        authClient.getAuthToken(TestData.EMAIL_1, TestData.PASSWORD_1),
                        new IngredientsDto(List.of(TestData.INGREDIENT, TestData.INGREDIENT2)))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("name", notNullValue())
                .and()
                .body("order", notNullValue())
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверка успешного создания заказа без авторизации")
    @Description("Проверка возврата статуса 200 и тела ответа")
    public void createOrderWithoutAuthSuccess() {

        orderClient.createOrderWithoutToken(new IngredientsDto(List.of(TestData.INGREDIENT, TestData.INGREDIENT2)))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("name", notNullValue())
                .and()
                .body("order", notNullValue())
                .and()
                .body("success", equalTo(true));
    }


    @Test
    @DisplayName("Проверка ошибки при создании заказа без ингредиентов")
    @Description("Проверка возврата статуса 400 и тела ответа")
    public void createOrderWithoutIngredientsFail() {

        orderClient.createOrderWithToken(
                        authClient.getAuthToken(TestData.EMAIL_1, TestData.PASSWORD_1),
                        new IngredientsDto(null))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .body("success", equalTo(false));

    }

    @Test
    @DisplayName("Проверка ошибки при создании заказа c неверным хешом ингредиентов")
    @Description("Проверка возврата статуса 500")
    public void createOrderWithWrongIngredientFail() {

        orderClient.createOrderWithToken(
                        authClient.getAuthToken(TestData.EMAIL_1, TestData.PASSWORD_1),
                        new IngredientsDto(List.of(TestData.WRONG_INGREDIENT)))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }
}
