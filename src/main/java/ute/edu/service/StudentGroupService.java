package ute.edu.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ute.edu.model.StudentGroup;
import ute.edu.repository.StudentGroupRepository;

@Service
public class StudentGroupService {
    private final StudentGroupRepository studentGroupRepository;

    public StudentGroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<StudentGroup> getAll() {
        return studentGroupRepository.findAll();
    }

    public StudentGroup save(StudentGroup group) {
        return studentGroupRepository.save(group);
    }
}
