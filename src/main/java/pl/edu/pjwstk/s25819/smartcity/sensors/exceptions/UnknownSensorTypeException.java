package pl.edu.pjwstk.s25819.smartcity.sensors.exceptions;

public class UnknownSensorTypeException extends RuntimeException {
    public UnknownSensorTypeException(String message) {
        super(message);
    }

    public UnknownSensorTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownSensorTypeException(Throwable cause) {
        super(cause);
    }
}
