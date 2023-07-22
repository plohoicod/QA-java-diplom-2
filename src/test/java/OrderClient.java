import dto.IngredientsDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    static final String ORDER_API = "/api/orders";

    @Step("Отправка GET в /api/orders с токеном")
    public Response getUserOrdersWithToken(String token) {
        return
                given()
                        .header("Authorization", token)
                        .when()
                        .get(ORDER_API);

    }

    @Step("Отправка GET в /api/orders без токена")
    public Response getUserOrdersWithoutToken() {
        return
                given()
                        .when()
                        .get(ORDER_API);

    }

    @Step("Отправка POST в /api/orders с токеном")
    public Response createOrderWithToken(String token, IngredientsDto dto) {
        return
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", token)
                        .and()
                        .body(dto)
                        .when()
                        .post(ORDER_API);

    }

    @Step("Отправка POST в /api/orders без токена")
    public Response createOrderWithoutToken(IngredientsDto dto) {
        return
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(dto)
                        .when()
                        .post(ORDER_API);

    }


}
