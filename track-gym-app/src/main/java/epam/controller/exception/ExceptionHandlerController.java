package epam.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Error handleEntityNotFoundException(UnauthorizedException e) {
        return createError(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    private Error createError(HttpStatus code, String message) {
        Error error = new Error();
        error.setCode(Integer.toString(code.value()));
        error.setMessage(message);
        return error;
    }

    public record ValidationErrorResponse(
            String message,
            List<FieldErrorDetail> errors
    ) {}

    public record FieldErrorDetail(
            String field,
            String message
    ) {}
}
