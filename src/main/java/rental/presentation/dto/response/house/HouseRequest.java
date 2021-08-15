package rental.presentation.dto.response.house;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rental.domain.model.enums.HouseStatus;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HouseRequest {
    private Long id;

    private String name;

    @NotNull(message = "房屋地址不能为空")
    private String location;

    @NotNull(message = "租金不能为空")
    private BigDecimal price;

    @NotNull(message = "建成时间不能为空")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime establishedTime;

    private HouseStatus status;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
