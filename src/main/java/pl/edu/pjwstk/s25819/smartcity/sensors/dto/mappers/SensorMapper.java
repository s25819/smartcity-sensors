package pl.edu.pjwstk.s25819.smartcity.sensors.dto.mappers;

import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorResponseDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.Sensor;

public class SensorMapper {

    public static SensorResponseDto toDto(Sensor sensor) {

        return new SensorResponseDto(
                String.valueOf(sensor.getId()),
                sensor.getSensorId(),
                sensor.getSensorType().getName(),
                String.valueOf(sensor.getLocation().getLatitude()),
                String.valueOf(sensor.getLocation().getLongitude()),
                sensor.getStatus().name()
        );
    }
}
