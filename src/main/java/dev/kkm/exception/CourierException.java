package dev.kkm.exception;

public class CourierException extends RuntimeException {
    private int status = 900;

    private CourierException() {}

    public CourierException(String message) {
        super(message);
    }
    public CourierException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
