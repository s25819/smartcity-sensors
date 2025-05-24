package pl.edu.pjwstk.s25819.smartcity.sensors.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.s25819.smartcity.sensors.avro.model.AirQualityObserved;
import pl.edu.pjwstk.s25819.smartcity.sensors.avro.model.GeoLocation;
import pl.edu.pjwstk.s25819.smartcity.sensors.config.KafkaTopicsConfig;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.Sensor;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.SensorType;
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
    private final Map<Long, ScheduledFuture<?>> runningSimulations = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final SensorRepository sensorRepository;

    private final KafkaTopicsConfig kafkaTopicsConfig;

    @Override
    public SimulationResponse startSimulation(Sensor sensor) {
        log.info("Uruchamianie symulacji dla czujnika: {}", sensor);

        if (runningSimulations.containsKey(sensor.getId())) {
            log.info("Symulacja dla czujnika o id: {} już działa", sensor.getSensorId());
            return new SimulationResponse(sensor.getSensorId(), "running",
                    String.format("Symulacja dla czujnika o id: %s już działa", sensor.getSensorId()));
        }

        log.info("Uruchamianie symulacji dla czujnika : {}", sensor);

        Runnable task;

        try {
            task = () -> {
                String topic = getTopicForSensorType(sensor.getSensorType());
                Object message = generateMessage(sensor);
                kafkaTemplate.send(topic, sensor.getSensorId(), message);
            };
        } catch (Exception e) {
            log.error("Błąd podczas uruchamiania symulacji: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas uruchamiania symulacji", e);
        }

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
        runningSimulations.put(sensor.getId(), future);
        log.info("Symulacja dla czujnika o id: {} uruchomiona", sensor.getSensorId());

        return new SimulationResponse(sensor.getSensorId(), "running", "Symulacja została uruchomiona");
    }

    private Object generateMessage(Sensor sensor) {

        switch (sensor.getSensorType()) {
            case AIR_QUALITY -> {

                return AirQualityObserved.newBuilder()
                        .setId("AirQualityObserved:" + sensor.getId())
                        .setSensorId("air-quality-" + sensor.getId())
                        .setType("AirQualityObserved")
                        .setSensorId(sensor.getSensorId())
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

    private String getTopicForSensorType(SensorType sensorType) {

        switch (sensorType) {
            case AIR_QUALITY -> {
                return kafkaTopicsConfig.getAirQualityTopic();
            }
            default -> throw new IllegalArgumentException("Nieznany typ czujnika: " + sensorType);
        }
    }

    @Override
    public SimulationResponse stopSimulation(Sensor sensor) {
        ScheduledFuture<?> future = runningSimulations.remove(sensor.getId());
        if (future != null)
            future.cancel(true);

        log.info("Symulacja dla czujnika o id: {} zatrzymana", sensor.getSensorId());
        return new SimulationResponse(sensor.getSensorId(), "stopped", "Symulacja została zatrzymana");
    }
}