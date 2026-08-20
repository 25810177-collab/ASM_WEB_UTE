package ute.edu.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ute.edu.model.RegistrationPeriod;
import ute.edu.repository.RegistrationPeriodRepository;

@Service
public class RegistrationPeriodService {
    private final RegistrationPeriodRepository registrationPeriodRepository;

    public RegistrationPeriodService(RegistrationPeriodRepository registrationPeriodRepository) {
        this.registrationPeriodRepository = registrationPeriodRepository;
    }

    public List<RegistrationPeriod> getAll() {
        return registrationPeriodRepository.findAll();
    }

    public RegistrationPeriod save(RegistrationPeriod registrationPeriod) {
        return registrationPeriodRepository.save(registrationPeriod);
    }
}
