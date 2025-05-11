package pl.edu.pjwstk.s25819.smartcity.sensors.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorRequestDto(

        @NotBlank(message = "Typ sensora nie może być pusty")
        String type,

        @NotNull(message = "Długość geograficzna nie może być pusta")
        Double latitude,

        @NotNull(message = "Szerokość geograficzna nie może być pusta")
        Double longitude
) {

}
