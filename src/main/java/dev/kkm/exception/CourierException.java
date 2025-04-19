package dev.kkm.exception;

public class CourierException extends RuntimeException {

    private CourierException() {}

    public CourierException(String message) {
        super(message);
    }
}
