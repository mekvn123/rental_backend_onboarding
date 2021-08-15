package rental.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rental.RentalServiceApplication;
import rental.client.FakeClient;
import rental.config.BaseIntegrationTest;
import rental.infrastructure.dataentity.HouseEntity;
import rental.infrastructure.persistence.HouseJpaPersistence;
import rental.presentation.dto.response.house.HouseRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RentalServiceApplication.class
)
public class HouseControllerApiTest extends BaseIntegrationTest {
    @Autowired
    private ApplicationContext applicationContext;

    private HouseJpaPersistence persistence;

    @MockBean
    private FakeClient fakeClient;

    @Before
    public void setUp() {
        persistence = applicationContext.getBean(HouseJpaPersistence.class);
    }

    @Test
    public void should_get_all_houses() {
        // given
        persistence.saveAndFlush(HouseEntity.builder().name("house-1").build());
        persistence.saveAndFlush(HouseEntity.builder().name("house-2").build());

        // when
        given()
                .when()
                .get("/houses")
                .then()
                .statusCode(200)
                .body("totalElements", is(2))
                .body("content", hasSize(2));
    }

    @Test
    public void should_find_house_success_if_id_exist() {
        // given
        HouseEntity houseEntity = persistence.saveAndFlush(HouseEntity.builder().name("house-1").build());

        // when
        given()
                .when()
                .get("/houses/" + houseEntity.getId())
                .then()
                .statusCode(200)
                .body("name", is("house-1"));
    }

    @Test
    public void should_find_house_fail_if_id_not_exist() {
        // given
        // when
        given()
                .when()
                .get("/houses/1")
                .then()
                .statusCode(404)
                .body("message", is("无此房源信息"));
    }

    @Test
    public void should_add_house_success_if_house_info_is_complete() throws Exception {
        // given
        HouseRequest houseRequest = HouseRequest.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .price(new BigDecimal("3000.0"))
                .establishedTime(LocalDateTime.now())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(houseRequest);

        // when
        when(fakeClient.addHouse(any())).thenReturn(Boolean.TRUE);

        // then
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/houses")
                .then()
                .status(HttpStatus.CREATED);
    }

    @Test
    public void should_add_house_fail_if_house_info_lack_location() throws Exception {
        // given
        HouseRequest houseRequest = HouseRequest.builder()
                .name("Home Sweet Home")
                .price(new BigDecimal("3000.0"))
                .establishedTime(LocalDateTime.now())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(houseRequest);

        // when
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/houses")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("message", is("房屋地址不能为空"));
    }

    @Test
    public void should_add_house_fail_if_house_info_lack_price() throws Exception {
        // given
        HouseRequest houseRequest = HouseRequest.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .establishedTime(LocalDateTime.now())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(houseRequest);

        // when
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/houses")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("message", is("租金不能为空"));
    }

    @Test
    public void should_add_house_fail_if_house_info_lack_establishedTime() throws Exception {
        // given
        HouseRequest houseRequest = HouseRequest.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .price(new BigDecimal("3000.0"))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(houseRequest);

        // when
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/houses")
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("message", is("建成时间不能为空"));
    }

    @Test
    public void should_add_house_fail_if_send_to_3rd_Client_fail() throws Exception {
        // given
        HouseRequest houseRequest = HouseRequest.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .price(new BigDecimal("3000.0"))
                .establishedTime(LocalDateTime.now())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(houseRequest);
        // when
        when(fakeClient.addHouse(any())).thenReturn(Boolean.FALSE);

        // then
        given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/houses")
                .then()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("message", is("fail: send houseInfo to 3rd Client"));
    }
}
