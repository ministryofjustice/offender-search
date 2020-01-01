package uk.gov.justice.hmpps.offendersearch.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.justice.hmpps.offendersearch.dto.OffenderDetail;
import uk.gov.justice.hmpps.offendersearch.util.LocalStackHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev,localstack")
@RunWith(SpringJUnit4ClassRunner.class)
public class OffenderSearchAPIIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalStackHelper localStackHelper;

    @Value("${test.token.good}")
    private String validOauthToken;

    @Before
    public void setup() throws IOException {
        localStackHelper.loadData();
        RestAssured.port = port;
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((aClass, s) -> objectMapper));
    }


    @Test
    public void offenderSearch() throws IOException {

       final var results = given()
                .auth()
                .oauth2(validOauthToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body("{\"surname\":\"smith\"}")
                .when()
                .get("/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(OffenderDetail[].class);

         assertThat(results).hasSize(2);
         assertThat(results).extracting("firstName").containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    public void noSearchParameters_badRequest() throws IOException {

        final var results = given()
                .auth()
                .oauth2(validOauthToken)
                .contentType(APPLICATION_JSON_VALUE)
                .body("{}")
                .when()
                .get("/search")
                .then()
                .statusCode(400)
                .body("developerMessage", containsString("Invalid search  - please provide at least 1 search parameter"));
    }

    @Test
    public void noSearchParameters_ccbadRequest() throws IOException {

        }

}