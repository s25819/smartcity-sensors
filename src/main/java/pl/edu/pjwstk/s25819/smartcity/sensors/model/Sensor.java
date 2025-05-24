package pl.edu.pjwstk.s25819.smartcity.sensors.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@ToString
public abstract class Sensor {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    public String getSensorId() {
        return sensorType.getName() + "-" + id;
    }

    public Long parseId(String sensorId) {
        return sensorId.split("-")[1].isEmpty() ? null : Long.parseLong(sensorId.split("-")[sensorId.split("-").length - 1]);
    }

    @Embedded
    private Geolocation location;

    @Enumerated(EnumType.STRING)
    protected SensorType sensorType;

    @Enumerated(EnumType.STRING)
    private SensorStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
