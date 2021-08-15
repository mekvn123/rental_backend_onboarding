package rental.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rental.domain.model.House;
import rental.domain.repository.HouseRepository;


@Service
@RequiredArgsConstructor
public class HouseApplicationService {
    private final HouseRepository houseRepository;

    public Page<House> queryAllHouses(Pageable pageable) {
        return houseRepository.queryAllHouses(pageable);
    }

    public House findHouseById(Long id) {
        return houseRepository.findHouseById(id).get();
    }
}

