package pl.edu.pjwstk.s25819.smartcity.sensors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjwstk.s25819.smartcity.sensors.model.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor<?>, Integer> {
}
