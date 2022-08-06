package pl.wj.ordermanager.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class ExceptionBody {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;
}
