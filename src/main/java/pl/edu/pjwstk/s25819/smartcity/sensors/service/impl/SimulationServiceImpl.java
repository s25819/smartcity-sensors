package pl.edu.pjwstk.s25819.smartcity.sensors.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.s25819.smartcity.sensors.exceptions.SensorNotFoundException;
import pl.edu.pjwstk.s25819.smartcity.sensors.repository.SensorRepository;
import pl.edu.pjwstk.s25819.smartcity.sensors.service.SimulationService;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationServiceImpl implements SimulationService {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final Map<Integer, ScheduledFuture<?>> runningSimulations = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    private final SensorRepository sensorRepository;

    @Override
    public SimulationResponse startSimulation(int sensorId) {

        log.info("Uruchamianie symulacji dla czujnika o id: {}", sensorId);

        if (runningSimulations.containsKey(sensorId)) {
            log.info("Symulacja dla czujnika o id: {} już działa", sensorId);
            return new SimulationResponse(String.valueOf(sensorId), "running", String.format("Symulacja dla czujnika o id: %s już działa", sensorId));
        }

        var sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new SensorNotFoundException("Nie znaleziono czujnika o id: " + sensorId));

        log.info("Uruchamianie symulacji dla czujnika : {}", sensor);

        Runnable task;

        try {

            task = () -> {
                var sensorType = sensor.getType().toLowerCase();

                String topic = getTopicForSensorType(sensorType);
                String message = generateMessage(sensorType, sensorId);
                kafkaTemplate.send(topic, sensorId, message);
            };
        } catch (Exception e) {
            log.error("Błąd podczas uruchamiania symulacji: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas uruchamiania symulacji", e);
        }

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);

        runningSimulations.put(sensorId, future);

        log.info("Symulacja dla czujnika o id: {} uruchomiona", sensorId);

        return new SimulationResponse(String.valueOf(sensorId), "running", "Symulacja została uruchomiona");
    }

    private String generateMessage(String sensorType, int sensorId) {
        switch (sensorType) {
            case "airqualitysensor" -> {
                return String.format("""
                        {
                          "id": "AirQualityObserved:%s",
                          "type": "AirQualityObserved",
                          "dateObserved": "%s",
                          "location": {"type": "Point", "coordinates": [21.01, 52.23]},
                          "pm10": %.2f,
                          "pm2_5": %.2f,
                          "temperature": %.1f,
                          "humidity": %d
                        }""", sensorId, Instant.now(), Math.random() * 100, Math.random() * 50, 15 + Math.random() * 10, 60 + (int) (Math.random() * 30));
            }
            case "traffic" -> {
                return String.format("""
                        {
                          "id": "TrafficFlowObserved:%s",
                          "type": "TrafficFlowObserved",
                          "dateObserved": "%s",
                          "location": {"type": "Point", "coordinates": [21.01, 52.23]},
                          "vehicleCount": %d,
                          "averageVehicleSpeed": %.1f,
                          "congested": %s
                        }""", sensorId, Instant.now(), (int) (Math.random() * 100), Math.random() * 100, Math.random() > 0.5);
            }
            default -> throw new IllegalArgumentException("Nieznany typ czujnika: " + sensorType);
        }
    }

    private String getTopicForSensorType(String sensorType) {
        return "sensor-" + sensorType.toLowerCase();
    }

    @Override
    public SimulationResponse stopSimulation(int sensorId) {
        ScheduledFuture<?> future = runningSimulations.remove(sensorId);
        if (future != null) future.cancel(true);

        log.info("Symulacja dla czujnika o id: {} zatrzymana", sensorId);
        return new SimulationResponse(String.valueOf(sensorId), "stopped", "Symulacja została zatrzymana");
    }
}
