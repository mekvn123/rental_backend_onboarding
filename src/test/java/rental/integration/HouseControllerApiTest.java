package rental.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rental.RentalServiceApplication;
import rental.config.BaseIntegrationTest;
import rental.infrastructure.dataentity.HouseEntity;
import rental.infrastructure.persistence.HouseJpaPersistence;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RentalServiceApplication.class
)
public class HouseControllerApiTest extends BaseIntegrationTest {
    @Autowired
    private ApplicationContext applicationContext;

    private HouseJpaPersistence persistence;

    @Before
    public void setUp() {
        persistence = applicationContext.getBean(HouseJpaPersistence.class);
    }

    @Test
    public void should_get_all_houses() throws Exception {
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
                .get("/houses/"+houseEntity.getId())
                .then()
                .statusCode(200)
                .body("id",is(1))
                .body("name",is("house-1"));
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
                .body("message",is("无此房源信息"));
    }
}
