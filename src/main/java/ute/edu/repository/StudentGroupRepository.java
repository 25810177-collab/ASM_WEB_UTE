package ute.edu.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.entity.StudentGroup;

/** Truy vấn bảng student_groups (nhóm sinh viên). */
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    boolean existsByMembersStudentIdAndRegistrationPeriodIdAndIdNot(Long studentId, Long periodId, Long groupId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from StudentGroup g where g.id = :id")
    Optional<StudentGroup> findForUpdate(@Param("id") Long id);
    /** Lấy nhóm trong một đợt đăng ký. */
    List<StudentGroup> findByRegistrationPeriodId(Long periodId);
    /** Tìm nhóm do sinh viên làm trưởng trong một đợt. */
    Optional<StudentGroup> findByLeaderIdAndRegistrationPeriodId(Long leaderId, Long periodId);
    /** Lấy các nhóm do một sinh viên làm trưởng. */
    List<StudentGroup> findByLeaderId(Long leaderId);
}
