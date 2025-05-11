package pl.edu.pjwstk.s25819.smartcity.sensors.service;

import pl.edu.pjwstk.s25819.smartcity.sensors.service.impl.SimulationResponse;

public interface SimulationService {

    SimulationResponse startSimulation(int sensorId);

    SimulationResponse stopSimulation(int sensorId);
}
