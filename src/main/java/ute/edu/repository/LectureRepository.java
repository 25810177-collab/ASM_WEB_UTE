package ute.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.model.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
