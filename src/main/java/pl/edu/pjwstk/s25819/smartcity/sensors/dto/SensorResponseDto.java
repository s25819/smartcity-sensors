package pl.edu.pjwstk.s25819.smartcity.sensors.dto;

public record SensorResponseDto(
        String id,
        String sensorId,
        String type,
        String latitude,
        String longitude,
        String status
) {
}
