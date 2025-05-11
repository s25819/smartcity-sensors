package pl.edu.pjwstk.s25819.smartcity.sensors.service;

import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorChangeStatusRequestDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorRequestDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorResponseDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorTypeResponseDto;

import java.util.List;

public interface SensorService {

    List<SensorResponseDto> getAllSensors();

    SensorResponseDto createSensor(SensorRequestDto sensorRequestDto);

    SensorResponseDto updateSensor(int id, SensorChangeStatusRequestDto sensorChangeStatusRequestDto);

    List<SensorTypeResponseDto> getSensorTypes();
}
