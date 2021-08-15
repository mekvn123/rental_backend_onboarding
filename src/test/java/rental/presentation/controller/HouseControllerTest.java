package rental.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import rental.application.HouseApplicationService;
import rental.domain.model.House;
import rental.presentation.dto.response.house.HouseRequest;
import rental.presentation.exception.AppException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HouseControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private HouseApplicationService applicationService;

    @Test
    public void should_get_all_houses() throws Exception {
        // given
        List<House> houseList = Arrays.asList(
                House.builder().id(1L).name("name-1").build(),
                House.builder().id(2L).name("name-2").build());
        Page<House> housePage = new PageImpl<>(houseList);
        when(applicationService.queryAllHouses(any())).thenReturn(housePage);

        // when
        mvc.perform(get("/houses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    public void should_find_house_success_if_id_exist() throws Exception {
        // given
        House house = House.builder().id(1L).name("name-1").build();
        when(applicationService.findHouseById(1L)).thenReturn(house);

        //when
        mvc.perform(get("/houses/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("name-1")));
    }

    @Test
    public void should_find_house_fail_if_id_not_exist() throws Exception {
        // given
        when(applicationService.findHouseById(1L)).thenThrow(new AppException("404", "无此房源信息"));

        // when
        mvc.perform(get("/houses/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("无此房源信息")));
    }

    @Test
    public void should_add_house_success_if_house_info_is_complete() throws Exception {
        // given
        House house = House.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .price(new BigDecimal("3000.0"))
                .establishedTime(LocalDateTime.now())
                .build();
        HouseRequest houseRequest = HouseRequest.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .price(new BigDecimal("3000.0"))
                .establishedTime(LocalDateTime.now())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(houseRequest);
        when(applicationService.addHouse(any())).thenReturn(house);

        // then
        mvc.perform(post("/houses").accept(MediaType.APPLICATION_JSON)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Home Sweet Home")));
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
        when(applicationService.addHouse(any())).thenThrow(new AppException("400", "房屋地址不能为空"));

        // then
        mvc.perform(post("/houses").accept(MediaType.APPLICATION_JSON)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("房屋地址不能为空")));
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
        when(applicationService.addHouse(any())).thenThrow(new AppException("400", "租金不能为空"));

        // then
        mvc.perform(post("/houses").accept(MediaType.APPLICATION_JSON)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("租金不能为空")));
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
        when(applicationService.addHouse(any())).thenThrow(new AppException("400", "建成时间不能为空"));

        // then
        mvc.perform(post("/houses").accept(MediaType.APPLICATION_JSON)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("建成时间不能为空")));
    }
}
