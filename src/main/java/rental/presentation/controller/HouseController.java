package rental.presentation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import rental.application.HouseApplicationService;
import rental.presentation.assembler.ModelToResponseMapper;
import rental.presentation.assembler.RequestToModelMapper;
import rental.presentation.dto.response.house.HouseRequest;
import rental.presentation.dto.response.house.HouseResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/houses")
@Slf4j
@Validated
@AllArgsConstructor
public class HouseController {
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final HouseApplicationService promotionProposalApplicationService;

    @GetMapping
    public Page<HouseResponse> queryAllHouses(
            @PageableDefault(size = DEFAULT_PAGE_SIZE)
            @SortDefault(sort = "createdTime", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return promotionProposalApplicationService.queryAllHouses(pageable)
                .map(ModelToResponseMapper.INSTANCE::mapToPromotionProposalResponse);
    }

    @GetMapping(path = "/{id}")
    public HouseResponse findHouseById(
            @PathVariable("id") Long id) {
        return ModelToResponseMapper.INSTANCE.mapToPromotionProposalResponse(
                promotionProposalApplicationService.findHouseById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HouseResponse addHouse(@RequestBody @Valid HouseRequest houseRequest) {
        return ModelToResponseMapper.INSTANCE.mapToPromotionProposalResponse(
                promotionProposalApplicationService.addHouse(
                        RequestToModelMapper.INSTANCE.mapToModel(houseRequest)));
    }
}
