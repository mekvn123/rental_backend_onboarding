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
import rental.infrastructure.mapper.ModelToEntityMapper;
import rental.infrastructure.persistence.HouseJpaPersistence;
import rental.presentation.exception.NotFoundException;

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
        HouseEntity houseEntity = persistence.findById(id).orElseThrow(() -> new NotFoundException("404", "无此房源信息"));
        return Optional.of(EntityToModelMapper.INSTANCE.mapToModel(houseEntity));
    }

    @Override
    public House addHouse(House house) {
        return EntityToModelMapper.INSTANCE.mapToModel(
                persistence.save(ModelToEntityMapper.INSTANCE.mapToEntity(house)));
    }

    @Override
    public void deleteHouse(House house) {
        persistence.delete(ModelToEntityMapper.INSTANCE.mapToEntity(house));
    }
}
