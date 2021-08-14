package rental.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rental.presentation.assembler.ModelToResponseMapper;
import rental.presentation.dto.response.common.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handle(AppException ex) {
        ErrorResponse errorResponse = ModelToResponseMapper.INSTANCE.mapToErrorResponse(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
