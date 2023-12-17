package ru.clevertec.cache.exception;

/**
 * This exception is typically thrown when there are issues with bad request.
 *
 * @author Yurkova Anastacia
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
