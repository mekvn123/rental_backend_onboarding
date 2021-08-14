package rental.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rental.domain.model.House;

public interface HouseRepository {
    Page<House> queryAllHouses(Pageable pageable);

    House findHouseById(Long id);
}
