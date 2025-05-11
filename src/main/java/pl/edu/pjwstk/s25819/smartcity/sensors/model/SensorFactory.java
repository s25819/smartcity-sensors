package pl.edu.pjwstk.s25819.smartcity.sensors.model;

import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorRequestDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.exceptions.UnknownSensorTypeException;

import java.time.LocalDateTime;

public class SensorFactory {

    public static Sensor<?> createSensor(SensorRequestDto sensorRequestDto) {
        String type = sensorRequestDto.type().toLowerCase();

        return switch (type) {
            case "airquality" -> handleAirQualitySensor(sensorRequestDto);
            default -> throw new UnknownSensorTypeException("Nieznany typ sensora: " + type);
        };
    }

    private static AirQualitySensor handleAirQualitySensor(SensorRequestDto sensorRequestDto) {
        AirQualitySensor airQualitySensor = new AirQualitySensor();
        airQualitySensor.setLocation(new Geolocation(sensorRequestDto.latitude(), sensorRequestDto.longitude()));
        airQualitySensor.setStatus(SensorStatus.INACTIVE);
        airQualitySensor.setCreatedAt(LocalDateTime.now());
        airQualitySensor.setUpdatedAt(LocalDateTime.now());
        airQualitySensor.setType("AirQualitySensor");
        return airQualitySensor;
    }
}
