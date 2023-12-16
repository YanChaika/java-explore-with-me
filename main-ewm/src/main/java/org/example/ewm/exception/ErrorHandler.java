package org.example.ewm.exception;

import org.example.ewm.enums.Status;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(
                List.of(HttpStatus.NOT_FOUND.toString()),
                "NOT_FOUND",
                e.getMessage(),
                Status.NOT_FOUND.toString(),
                LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(
                List.of(HttpStatus.CONFLICT.toString()),
                "CONFLICT",
                e.getMessage(),
                Status.CONFLICT.toString(),
                LocalDateTime.now().toString()
        );
    }

}
