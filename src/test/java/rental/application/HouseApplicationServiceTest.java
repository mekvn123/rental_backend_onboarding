package rental.application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rental.domain.model.House;
import rental.domain.repository.HouseRepository;
import rental.presentation.exception.AppException;

import java.util.Arrays;
import java.util.List;

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
        when(repository.findHouseById(any())).thenReturn(house);

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
}
