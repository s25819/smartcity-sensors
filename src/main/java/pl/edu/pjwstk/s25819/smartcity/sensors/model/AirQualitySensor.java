package pl.edu.pjwstk.s25819.smartcity.sensors.model;

import jakarta.persistence.Entity;

@Entity
public class AirQualitySensor extends Sensor<Integer> {

    public AirQualitySensor() {
        super();
        this.type = "AirQualitySensor";
    }
}
