package rental.presentation.assembler;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import rental.domain.model.House;
import rental.presentation.dto.response.common.ErrorResponse;
import rental.presentation.dto.response.house.HouseResponse;
import rental.presentation.exception.Add3rdClientException;
import rental.presentation.exception.NotFoundException;
import rental.utils.DateUtils;

import java.time.LocalDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ModelToResponseMapper {

    ModelToResponseMapper INSTANCE = Mappers.getMapper(ModelToResponseMapper.class);

    @Mapping(target = "establishedTime", source = "model.establishedTime", qualifiedByName = "toTimestamp")
    @Mapping(target = "createdTime", source = "model.createdTime", qualifiedByName = "toTimestamp")
    @Mapping(target = "updatedTime", source = "model.createdTime", qualifiedByName = "toTimestamp")
    HouseResponse mapToPromotionProposalResponse(House model);

    @Named("toTimestamp")
    default long toTimestamp(LocalDateTime localDateTime) {
        return DateUtils.toTimestamp(localDateTime);
    }

    ErrorResponse mapToErrorResponse(NotFoundException notFoundException);

    ErrorResponse mapToErrorResponse(Add3rdClientException add3rdClientException);
}
