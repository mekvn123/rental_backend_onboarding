package rental.infrastructure.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import rental.domain.model.House;
import rental.domain.repository.HouseRepository;
import rental.infrastructure.mapper.EntityToModelMapper;
import rental.infrastructure.persistence.HouseJpaPersistence;
import rental.presentation.exception.AppException;

@Component
@Slf4j
@AllArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {
    private final HouseJpaPersistence persistence;

    @Override
    public Page<House> queryAllHouses(Pageable pageable) {
        return this.persistence.findAll(pageable).map(EntityToModelMapper.INSTANCE::mapToModel);
    }

    @Override
    public House findHouseById(Long id) {
        return EntityToModelMapper.INSTANCE.mapToModel(this.persistence.findById(id).orElseThrow(() -> new AppException("404", "无此房源信息")));
    }
}
