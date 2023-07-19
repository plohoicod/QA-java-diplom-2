import dto.AuthResponseDto;
import dto.LoginDto;
import dto.RegisterDto;
import dto.UserInfoDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

    static final String AUTH_API = "/api/auth";

    @Step("Отправка POST в /api/auth/register")
    public Response registerUser(RegisterDto dto) {
        return
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(dto)
                        .when()
                        .post(AUTH_API + "/register");

    }

    @Step("Отправка DELETE в /api/auth/user")
    public Response deleteUser(String token) {
        return
                given()
                        .header("Authorization", token)
                        .when()
                        .delete(AUTH_API + "/user");

    }

    @Step("Отправка PATCH в /api/auth/user с токеном")
    public Response patchUserInfoWithToken(String token, UserInfoDto dto) {
        return
                given()
                        .header("Authorization", token)
                        .header("Content-type", "application/json")
                        .and()
                        .body(dto)
                        .when()
                        .patch(AUTH_API + "/user");

    }

    @Step("Отправка PATCH в /api/auth/user без токена")
    public Response patchUserInfoWithoutToken(UserInfoDto dto) {
        return
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(dto)
                        .when()
                        .patch(AUTH_API + "/user");

    }

    @Step("Отправка POST в /api/auth/login")
    public Response loginUser(LoginDto dto) {
        return
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(dto)
                        .when()
                        .post(AUTH_API + "/login");

    }

    public String getAuthToken(String email, String password) {
        LoginDto loginDto = new LoginDto(email, password);
        AuthResponseDto authResponseDto = loginUser(loginDto).as(AuthResponseDto.class);

        return authResponseDto.getAccessToken();
    }

    public void deleteUser(String email, String password) {
        String token = getAuthToken(email, password);
        if (token != null)
            deleteUser(token);
    }


}
