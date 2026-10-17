package ute.edu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.entity.ReviewCouncil;

/** Truy vấn bảng councils (hội đồng phản biện). */
public interface ReviewCouncilRepository extends JpaRepository<ReviewCouncil, Long> {
    boolean existsByCodeIgnoreCase(String code);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select c from ReviewCouncil c where c.id = :id")
    java.util.Optional<ReviewCouncil> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
    /** Lấy hội đồng theo đợt đăng ký. */
    List<ReviewCouncil> findByRegistrationPeriodIdOrderByCreatedAtDescIdDesc(Long periodId);
    /** Lấy hội đồng theo khoa quản lý. */
    List<ReviewCouncil> findByDepartmentId(Long departmentId);
}
