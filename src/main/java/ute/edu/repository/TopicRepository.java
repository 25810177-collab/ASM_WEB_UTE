package ute.edu.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.enums.TopicStatus;
import ute.edu.entity.Topic;

/** Truy vấn dữ liệu bảng topics (đề tài). */
public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Topic t where t.id = :id")
    Optional<Topic> findForUpdate(@Param("id") Long id);

    @Query("select t from Topic t where t.status in (ute.edu.enums.TopicStatus.PUBLISHED, ute.edu.enums.TopicStatus.APPROVED) and t.registrationPeriod.status = ute.edu.enums.PeriodStatus.OPEN and t.registrationPeriod.active = true order by t.createdAt desc, t.id desc")
    List<Topic> findAvailableTopics();
    /** Lấy các đề tài theo trạng thái. */
    List<Topic> findByStatusOrderByCreatedAtDescIdDesc(TopicStatus status);
    /** Lấy các đề tài thuộc một khoa. */
    List<Topic> findByDepartmentIdOrderByCreatedAtDescIdDesc(Long departmentId);
    /** Lấy các đề tài thuộc một đợt đăng ký. */
    List<Topic> findByRegistrationPeriodIdOrderByCreatedAtDescIdDesc(Long periodId);
    /** Lấy các đề tài do một giảng viên hướng dẫn. */
    List<Topic> findByLecturerIdOrderByCreatedAtDescIdDesc(Long lecturerId);
    /** Lấy đề tài do giảng viên là GVHD chính hoặc đồng GVHD. */
    List<Topic> findByLecturerIdOrCoLecturerIdOrderByCreatedAtDescIdDesc(Long lecturerId, Long coLecturerId);
    /** Lọc đề tài theo khoa và trạng thái. */
    List<Topic> findByDepartmentIdAndStatusOrderByCreatedAtDescIdDesc(Long departmentId, TopicStatus status);
    /** Lọc đề tài theo đợt đăng ký và trạng thái. */
    List<Topic> findByRegistrationPeriodIdAndStatusOrderByCreatedAtDescIdDesc(Long periodId, TopicStatus status);
    /** Tìm đề tài theo tên hoặc mã, không phân biệt hoa thường. */
    List<Topic> findByTitleContainingIgnoreCaseOrCodeContainingIgnoreCaseOrderByCreatedAtDescIdDesc(String title, String code);
}
