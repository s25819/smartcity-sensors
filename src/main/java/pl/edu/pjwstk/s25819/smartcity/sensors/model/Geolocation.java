package pl.edu.pjwstk.s25819.smartcity.sensors.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Geolocation {
    private double latitude;
    private double longitude;
}
