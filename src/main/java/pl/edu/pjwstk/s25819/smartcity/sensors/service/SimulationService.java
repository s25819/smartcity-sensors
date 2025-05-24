package pl.edu.pjwstk.s25819.smartcity.sensors.service;

import pl.edu.pjwstk.s25819.smartcity.sensors.model.Sensor;
import pl.edu.pjwstk.s25819.smartcity.sensors.service.impl.SimulationResponse;

public interface SimulationService {

    SimulationResponse startSimulation(Sensor sensor);

    SimulationResponse stopSimulation(Sensor sensor);
}
