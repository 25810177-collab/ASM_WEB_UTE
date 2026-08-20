package ute.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.model.RegistrationPeriod;

public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
}
