package pl.edu.pjwstk.s25819.smartcity.sensors.dto;

import jakarta.validation.constraints.NotBlank;

public record SensorChangeStatusRequestDto(

        @NotBlank(message = "Status nie może być pusty")
        String status
) {

}
