package rental.application;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rental.client.FakeClient;
import rental.domain.model.House;
import rental.domain.repository.HouseRepository;
import rental.presentation.exception.Add3rdClientException;
import rental.presentation.exception.AppException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HouseApplicationServiceTest {
    @InjectMocks
    private HouseApplicationService applicationService;

    @Mock
    private HouseRepository repository;

    @Mock
    private FakeClient fakeClient;

    @Test
    public void should_get_all_houses() {
        // given
        List<House> houseList = Arrays.asList(
                House.builder().id(1L).name("name-1").build(),
                House.builder().id(2L).name("name-2").build());
        Page<House> housePage = new PageImpl<>(houseList);
        when(repository.queryAllHouses(any())).thenReturn(housePage);
        PageRequest pageable = PageRequest.of(0, 20);

        // when
        Page<House> result = applicationService.queryAllHouses(pageable);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void should_find_house_success_if_id_exist() {
        // given
        House house = House.builder().id(1L).name("name-1").build();
        when(repository.findHouseById(any())).thenReturn(Optional.of(house));

        // when
        House findHouse = applicationService.findHouseById(1L);

        // then
        assertEquals("name-1", findHouse.getName());
    }

    @Test
    public void should_find_house_fail_if_id_not_exist() {
        // given
        when(repository.findHouseById(Long.MIN_VALUE)).thenThrow(new AppException("404", "无此房源信息"));

        // then
        assertThatThrownBy(() ->
                repository.findHouseById(Long.MIN_VALUE)).isInstanceOf(AppException.class)
                .hasMessageContaining("无此房源信息");
    }

    @Test
    public void should_add_house_success_if_house_info_is_complete() {
        // given
        House house = House.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .price(new BigDecimal("3000.0"))
                .establishedTime(LocalDateTime.of(2008, 5, 20, 0, 0))
                .build();
        when(repository.addHouse(any())).thenReturn(house);
        when(fakeClient.addHouse(any())).thenReturn(Boolean.TRUE);

        // when
        House addHouse = applicationService.addHouse(house);

        // then
        Assertions.assertEquals("Beijing West 2nd Ring Road", addHouse.getLocation());
        Assertions.assertEquals(new BigDecimal("3000.0"), addHouse.getPrice());
        Assertions.assertEquals(LocalDateTime.of(2008, 5, 20, 0, 0), addHouse.getEstablishedTime());
    }

    @Test
    public void should_add_house_fail_if_send_to_3rd_Client_fail() {
        // given
        House house = House.builder()
                .name("Home Sweet Home")
                .location("Beijing West 2nd Ring Road")
                .price(new BigDecimal("3000.0"))
                .establishedTime(LocalDateTime.of(2008, 5, 20, 0, 0))
                .build();
        when(repository.addHouse(any())).thenReturn(house);
        when(fakeClient.addHouse(any())).thenReturn(Boolean.FALSE);

        // then
        Assertions.assertThrows(Add3rdClientException.class, () -> applicationService.addHouse(house));
    }
}
