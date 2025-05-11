package pl.edu.pjwstk.s25819.smartcity.sensors.dto.mappers;

import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorResponseDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.Sensor;

public class SensorMapper {

    public static SensorResponseDto toDto(Sensor<?> sensor) {

        return new SensorResponseDto(
                sensor.getId().toString(),
                sensor.getType(),
                String.valueOf(sensor.getLocation().getLatitude()),
                String.valueOf(sensor.getLocation().getLongitude()),
                sensor.getStatus().name()
        );
    }
}
