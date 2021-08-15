package rental.presentation.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rental.domain.model.House;
import rental.presentation.dto.response.house.HouseRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestToModelMapper {
    RequestToModelMapper INSTANCE = Mappers.getMapper(RequestToModelMapper.class);

    House mapToModel(HouseRequest houseRequest);
}
