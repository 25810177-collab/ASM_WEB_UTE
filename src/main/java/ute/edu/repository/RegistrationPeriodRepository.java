package ute.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.entity.RegistrationPeriod;

public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM registration_periods WHERE id=:id FOR UPDATE", nativeQuery = true)
    java.util.Optional<RegistrationPeriod> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
