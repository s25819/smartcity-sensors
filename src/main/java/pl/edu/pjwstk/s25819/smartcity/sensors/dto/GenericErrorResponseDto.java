package pl.edu.pjwstk.s25819.smartcity.sensors.dto;

public record GenericErrorResponseDto(
        String serviceName,
        String message,
        String error,
        String path,
        int status,
        long timestamp
) {
}
