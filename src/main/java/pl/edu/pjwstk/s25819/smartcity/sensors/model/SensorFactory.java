package pl.edu.pjwstk.s25819.smartcity.sensors.model;

import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorRequestDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.exceptions.UnknownSensorTypeException;

import java.time.LocalDateTime;
import java.util.Arrays;

public class SensorFactory {

    public static Sensor createSensor(SensorRequestDto sensorRequestDto) {
        SensorType sensorType = mapSensorType(sensorRequestDto.type());

        return switch (sensorType) {
            case AIR_QUALITY -> handleAirQualitySensor(sensorRequestDto);
            default -> throw new UnknownSensorTypeException("Nieznany typ sensora: " + sensorRequestDto.type());
        };
    }

    private static SensorType mapSensorType(String type) {
        return Arrays.stream(SensorType.values())
                .filter(sensorType -> sensorType.getName().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new UnknownSensorTypeException("Nieznany typ sensora: " + type));
    }

    private static AirQualitySensor handleAirQualitySensor(SensorRequestDto sensorRequestDto) {
        AirQualitySensor airQualitySensor = new AirQualitySensor();
        airQualitySensor.setLocation(new Geolocation(sensorRequestDto.latitude(), sensorRequestDto.longitude()));
        airQualitySensor.setStatus(SensorStatus.INACTIVE);
        airQualitySensor.setSensorType(SensorType.AIR_QUALITY);
        airQualitySensor.setCreatedAt(LocalDateTime.now());
        airQualitySensor.setUpdatedAt(LocalDateTime.now());
        return airQualitySensor;
    }
}
