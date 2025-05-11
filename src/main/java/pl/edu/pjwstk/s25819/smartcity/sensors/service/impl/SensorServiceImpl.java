package pl.edu.pjwstk.s25819.smartcity.sensors.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.*;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.mappers.SensorMapper;
import pl.edu.pjwstk.s25819.smartcity.sensors.exceptions.SensorNotFoundException;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.SensorFactory;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.SensorStatus;
import pl.edu.pjwstk.s25819.smartcity.sensors.repository.SensorRepository;
import pl.edu.pjwstk.s25819.smartcity.sensors.service.SensorService;
import pl.edu.pjwstk.s25819.smartcity.sensors.service.SimulationService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;
    private final SimulationService simulationService;

    @Override
    public List<SensorResponseDto> getAllSensors() {
        var result = sensorRepository.findAll();

        return result.isEmpty() ? List.of() : result.stream().map(SensorMapper::toDto).toList();

    }

    @Override
    public SensorResponseDto createSensor(SensorRequestDto sensorRequestDto) {
        var sensor = SensorFactory.createSensor(sensorRequestDto);
        var savedSensor = sensorRepository.save(sensor);

        return SensorMapper.toDto(savedSensor);
    }

    @Override
    public SensorResponseDto updateSensor(int id, SensorChangeStatusRequestDto sensorChangeStatusRequestDto) {
        var sensor = sensorRepository
                .findById(id)
                .orElseThrow(() -> new SensorNotFoundException(String.format("Nie znaleziono sensora o id: %s", id)));

        if (sensor.getStatus() == decodeStatus(sensorChangeStatusRequestDto.status())) {
            throw new IllegalArgumentException("Nie można ustawić tego samego statusu");
        }

        sensor.setStatus(decodeStatus(sensorChangeStatusRequestDto.status()));
        sensor.setUpdatedAt(sensor.getCreatedAt());
        var savedSensor = sensorRepository.save(sensor);

        if (sensor.getStatus() == SensorStatus.ACTIVE) {
            simulationService.startSimulation(id);
        } else {
            simulationService.stopSimulation(id);
        }

        return SensorMapper.toDto(savedSensor);
    }

    @Override
    public List<SensorTypeResponseDto> getSensorTypes() {

        var sensorTypes = new ArrayList<SensorTypeResponseDto>();

        var sensorType = new SensorTypeResponseDto(
                "airquality",
                "Czujnik jakości powietrza",
                "Czujnik jakości powietrza mierzący stężenie zanieczyszczeń w powietrzu"
          );

        sensorTypes.add(sensorType);

        return sensorTypes ;

    }

    private SensorStatus decodeStatus(String status) {
        if (status.equalsIgnoreCase("active"))
            return SensorStatus.ACTIVE;
        else if (status.equalsIgnoreCase("inactive"))
            return SensorStatus.INACTIVE;
        else throw new IllegalArgumentException("Nieznany status: " + status);
    }
}
