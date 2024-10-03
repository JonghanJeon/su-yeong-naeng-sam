package develop.grassserver.grass.exception;

import develop.grassserver.utils.ApiUtils;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GrassExceptionHandler {

    @ExceptionHandler(MissingAttendanceException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> missingAttendanceException(MissingAttendanceException exception) {
        return new ResponseEntity<>(exception.body(), exception.status());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleDateTimeParseException(DateTimeParseException ex) {
        return new ResponseEntity<>(
                ApiUtils.error(HttpStatus.BAD_REQUEST, "잘못된 시간 형식입니다."),
                HttpStatus.BAD_REQUEST
        );
    }
}
