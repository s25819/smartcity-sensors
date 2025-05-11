package pl.edu.pjwstk.s25819.smartcity.sensors.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.s25819.smartcity.sensors.exceptions.SensorNotFoundException;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.Sensor;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SimulationResponse startSimulation(int sensorId) {
        log.info("Uruchamianie symulacji dla czujnika o id: {}", sensorId);

        if (runningSimulations.containsKey(sensorId)) {
            log.info("Symulacja dla czujnika o id: {} już działa", sensorId);
            return new SimulationResponse(String.valueOf(sensorId), "running",
                    String.format("Symulacja dla czujnika o id: %s już działa", sensorId));
        }

        var sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException("Nie znaleziono czujnika o id: " + sensorId));

        log.info("Uruchamianie symulacji dla czujnika : {}", sensor);

        Runnable task;

        try {
            task = () -> {
                var sensorType = sensor.getType().toLowerCase();
                String topic = getTopicForSensorType(sensorType);
                String message = generateMessage(sensor);
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

    private String generateMessage(Sensor<?> sensor) {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("id", sensor.getType().equals("traffic") ? "TrafficFlowObserved:" + sensor.getId() : "AirQualityObserved:" + sensor.getId());
        json.put("type", sensor.getType().equals("traffic") ? "TrafficFlowObserved" : "AirQualityObserved");
        json.put("dateObserved", Instant.now().toString());

        ObjectNode location = json.putObject("location");
        location.put("type", "Point");
        ArrayNode coordinates = location.putArray("coordinates");
        coordinates.add(sensor.getLocation().getLatitude()).add(sensor.getLocation().getLongitude());

        switch (sensor.getType().toLowerCase()) {
            case "airqualitysensor" -> {
                json.put("pm10", Math.random() * 100);
                json.put("pm2_5", Math.random() * 50);
                json.put("temperature", 15 + Math.random() * 10);
                json.put("humidity", 60 + (int) (Math.random() * 30));
            }
            case "traffic" -> {
                json.put("vehicleCount", (int) (Math.random() * 100));
                json.put("averageVehicleSpeed", Math.random() * 100);
                json.put("congested", Math.random() > 0.5);
            }
            default -> throw new IllegalArgumentException("Nieznany typ czujnika");
        }

        try {
            return objectMapper.writeValueAsString(json);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas serializacji JSON", e);
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