package rental.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rental.client.FakeClient;
import rental.domain.model.House;
import rental.domain.repository.HouseRepository;
import rental.presentation.exception.Add3rdClientException;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class HouseApplicationService {
    private final HouseRepository houseRepository;
    private final FakeClient fakeClient;


    public Page<House> queryAllHouses(Pageable pageable) {
        return houseRepository.queryAllHouses(pageable);
    }

    public House findHouseById(Long id) {
        return houseRepository.findHouseById(id).get();
    }

    public House addHouse(House house) throws Add3rdClientException {
        House addHouse = houseRepository.addHouse(house);
        Optional<Boolean> isAddSuccess = Optional.empty();
        try {
            isAddSuccess = Optional.of(fakeClient.addHouse(house));
        } catch (Exception ex) {
            System.out.println(ex.getClass());
        }
        if (isAddSuccess.isPresent()) {
            if (!isAddSuccess.get()) {
                houseRepository.deleteHouse(house);
                throw new Add3rdClientException("500", "fail: send houseInfo to 3rd Client");
            }
        }
        return addHouse;
    }
}

