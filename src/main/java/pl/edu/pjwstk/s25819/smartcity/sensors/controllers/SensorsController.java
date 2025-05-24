package pl.edu.pjwstk.s25819.smartcity.sensors.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorChangeStatusRequestDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorRequestDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorResponseDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.dto.SensorTypeResponseDto;
import pl.edu.pjwstk.s25819.smartcity.sensors.service.SensorService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/sensors")
@Slf4j
@RequiredArgsConstructor
public class SensorsController {

    private final SensorService sensorService;

    @GetMapping({""})
    public ResponseEntity<List<SensorResponseDto>> getAllSensors(HttpServletRequest request) {
        log.info("Przyszedł request z {}", request.getRemoteAddr());

        var results = sensorService.getAllSensors();

        log.info("Zwracane dane o sensorach: {}", results);
        return ResponseEntity.ok(results);
    }

    @PostMapping({""})
    public ResponseEntity<SensorResponseDto> createSensor(@Valid @RequestBody SensorRequestDto sensorRequestDto) {
        log.info("Próba dodania nowego sensora: {}", sensorRequestDto);

        var sensorResponseDto = sensorService.createSensor(sensorRequestDto);

        log.info("Zwracane dane o sensorze: {}", sensorResponseDto);

        return ResponseEntity.ok(sensorResponseDto);
    }

    @PatchMapping({"/{id}"})
    public ResponseEntity<SensorResponseDto> updateSensor(@PathVariable long id, @Valid @RequestBody SensorChangeStatusRequestDto sensorChangeStatusRequestDto) {
        var sensorResponseDto = sensorService.updateSensor(id, sensorChangeStatusRequestDto);

        return ResponseEntity.ok(sensorResponseDto);
    }

    @GetMapping("/types")
    public ResponseEntity<List<SensorTypeResponseDto>> getSensorTypes() {
        var sensorTypesResponseDto = sensorService.getSensorTypes();
        return ResponseEntity.ok(sensorTypesResponseDto);
    }
}
