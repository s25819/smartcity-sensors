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
public abstract class Sensor<ID> {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private ID id;

    @Embedded
    private Geolocation location;

    protected String type;

    @Enumerated(EnumType.STRING)
    private SensorStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
