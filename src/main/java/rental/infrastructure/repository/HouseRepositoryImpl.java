package rental.infrastructure.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rental.domain.model.House;
import rental.domain.repository.HouseRepository;
import rental.infrastructure.dataentity.HouseEntity;
import rental.infrastructure.mapper.EntityToModelMapper;
import rental.infrastructure.persistence.HouseJpaPersistence;
import rental.presentation.exception.AppException;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {
    private final HouseJpaPersistence persistence;

    @Override
    public Page<House> queryAllHouses(Pageable pageable) {
        return persistence.findAll(pageable).map(EntityToModelMapper.INSTANCE::mapToModel);
    }

    @Override
    public Optional<House> findHouseById(Long id) {
        HouseEntity houseEntity = persistence.findById(id).orElseThrow(() -> new AppException("404", "无此房源信息"));
        return Optional.of(EntityToModelMapper.INSTANCE.mapToModel(houseEntity));
    }
}
