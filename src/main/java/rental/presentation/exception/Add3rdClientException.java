package rental.presentation.exception;

import lombok.Getter;

@Getter
public class Add3rdClientException extends RuntimeException {
    private final String code;

    public Add3rdClientException(String code, String message) {
        super(message);
        this.code = code;
    }
}
