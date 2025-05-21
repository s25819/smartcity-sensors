package pl.edu.pjwstk.s25819.smartcity.sensors.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.s25819.smartcity.sensors.avro.model.AirQualityObserved;
import pl.edu.pjwstk.s25819.smartcity.sensors.avro.model.GeoLocation;
import pl.edu.pjwstk.s25819.smartcity.sensors.config.KafkaTopicsConfig;
import pl.edu.pjwstk.s25819.smartcity.sensors.exceptions.SensorNotFoundException;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.Sensor;
import pl.edu.pjwstk.s25819.smartcity.sensors.repository.SensorRepository;
import pl.edu.pjwstk.s25819.smartcity.sensors.service.SimulationService;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationServiceImpl implements SimulationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<Integer, ScheduledFuture<?>> runningSimulations = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final SensorRepository sensorRepository;

    private final KafkaTopicsConfig kafkaTopicsConfig;

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
                Object message = generateMessage(sensor);
                kafkaTemplate.send(topic, sensorType + "-" + sensorId, message);
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

    private Object generateMessage(Sensor<?> sensor) {

        switch (sensor.getType().toLowerCase()) {
            case "airqualitysensor" -> {

                return AirQualityObserved.newBuilder()
                        .setId("AirQualityObserved:" + sensor.getId())
                        .setType("AirQualityObserved")
                        .setDateObserved(Instant.now())
                        .setLocation(new GeoLocation("Point", List.of(sensor.getLocation().getLatitude(), sensor.getLocation().getLongitude())))

                        .setPm10(Math.random() * 100)
                        .setPm25(Math.random() * 50)
                        .setTemperature(15 + Math.random() * 10)
                        .setHumidity(60 + (int) (Math.random() * 30))
                        .build();
            }
            default -> throw new IllegalArgumentException("Nieznany typ czujnika");
        }
    }

    private String getTopicForSensorType(String sensorType) {

        if ("AirQualitySensor".equalsIgnoreCase(sensorType)) {
            return kafkaTopicsConfig.getAirQualityTopic();
        } else
            throw new IllegalArgumentException("Nieznany typ czujnika: " + sensorType);
    }

    @Override
    public SimulationResponse stopSimulation(int sensorId) {
        ScheduledFuture<?> future = runningSimulations.remove(sensorId);
        if (future != null)
            future.cancel(true);

        log.info("Symulacja dla czujnika o id: {} zatrzymana", sensorId);
        return new SimulationResponse(String.valueOf(sensorId), "stopped", "Symulacja została zatrzymana");
    }
}