package ute.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
