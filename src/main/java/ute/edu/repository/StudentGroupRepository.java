package ute.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.model.StudentGroup;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
}
