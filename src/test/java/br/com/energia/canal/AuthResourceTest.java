package br.com.energia.canal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthResourceTest {

    @Test
    void deveCarregarDadosDemo() {
        given()
                .when()
                .get("/api/info")
                .then()
                .statusCode(200)
                .body("clientesCadastrados", equalTo(2));
    }

    @Test
    void loginComCredenciaisValidas() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"identificador":"maria@email.com","senha":"123456"}
                        """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("cliente.email", org.hamcrest.Matchers.equalTo("maria@email.com"));
    }

    @Test
    void loginComCredenciaisInvalidas() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"identificador":"maria@email.com","senha":"senha-errada"}
                        """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("message", org.hamcrest.Matchers.equalTo("CPF/e-mail ou senha inválidos"));
    }

    @Test
    void dashboardRequerAutenticacao() {
        given().when().get("/api/dashboard").then().statusCode(401);
    }

    @Test
    void fluxoLoginEDashboard() {
        String token = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"identificador":"maria@email.com","senha":"123456"}
                        """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("cliente.nome", org.hamcrest.Matchers.equalTo("Maria Silva Santos"))
                .body("faturasPendentes", greaterThan(0))
                .body("historicoConsumo", notNullValue());
    }

    @Test
    void infoExpoeBuildId() {
        given()
                .when()
                .get("/api/info")
                .then()
                .statusCode(200)
                .body("buildId", notNullValue())
                .body("clientesCadastrados", org.hamcrest.Matchers.equalTo(2));
    }
}
