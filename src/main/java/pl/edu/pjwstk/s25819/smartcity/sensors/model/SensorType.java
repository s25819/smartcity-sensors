package pl.edu.pjwstk.s25819.smartcity.sensors.model;

import lombok.Getter;

@Getter
public enum SensorType {

    AIR_QUALITY("air-quality"), TRAFFIC_FLOW("traffic-flow");

    private final String name;

    SensorType(String name) {
        this.name = name;
    }
}