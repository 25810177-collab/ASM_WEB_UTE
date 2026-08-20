package ute.edu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.enums.TopicStatus;
import ute.edu.model.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByStatus(TopicStatus status);
    List<Topic> findByDepartmentId(Long departmentId);
}
