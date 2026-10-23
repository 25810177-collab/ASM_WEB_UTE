package ute.edu.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ute.edu.entity.RegistrationPeriod;
import ute.edu.enums.RegistrationType;
import ute.edu.enums.PeriodStatus;
import ute.edu.repository.RegistrationPeriodRepository;
import ute.edu.validation.FormValidationException;

@Service
/** Service quản lý các đợt đăng ký đề tài và thời hạn nghiệp vụ. */
public class RegistrationPeriodService {
    /** Ngày nghiệp vụ theo múi giờ Việt Nam, dùng chung cho mọi kiểm tra thời hạn. */
    public static LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

    /** Khóa chỉnh sửa từ chính ngày GV bắt đầu đề xuất, kể cả đợt còn ở trạng thái nháp. */
    public static boolean canEditPeriod(RegistrationPeriod period) {
        return period != null && period.getStatus() != PeriodStatus.CLOSED
                && period.getLecturerStartDate() != null && today().isBefore(period.getLecturerStartDate());
    }

    /** Cửa sổ đăng ký sinh viên, bao gồm cả ngày bắt đầu và ngày kết thúc. */
    public static boolean isStudentWindow(RegistrationPeriod period) {
        LocalDate today = today();
        return period != null && period.isActive() && period.getStatus() == PeriodStatus.OPEN
                && period.getStudentStartDate() != null && period.getStudentEndDate() != null
                && !today.isBefore(period.getStudentStartDate()) && !today.isAfter(period.getStudentEndDate());
    }

    /** Chỉ cho đề xuất khi đợt hoạt động và còn trong thời gian giảng viên đề xuất. */
    public static boolean isLecturerWindow(RegistrationPeriod period) {
        LocalDate today = today();
        return period != null && period.isActive() && period.getStatus() == PeriodStatus.OPEN
                && period.getLecturerStartDate() != null && period.getLecturerEndDate() != null
                && !today.isBefore(period.getLecturerStartDate()) && !today.isAfter(period.getLecturerEndDate());
    }

    /** Danh sách cho ô chọn đợt trong form giảng viên, đợt mới nhất trước. */
    public List<RegistrationPeriod> getOpenLecturerPeriods() {
        return getAll().stream().filter(RegistrationPeriodService::isLecturerWindow).toList();
    }

    /** Hội đồng dùng ngày báo cáo của đợt; cho phép tạo đến hết chính ngày báo cáo. */
    public static boolean canCreateCouncil(RegistrationPeriod period) {
        return period != null && period.isActive() && period.getStatus() == PeriodStatus.OPEN
                && period.getCouncilReportDate() != null && !period.getCouncilReportDate().isBefore(today());
    }

    /** Lọc riêng cho form tạo hội đồng, không ảnh hưởng bộ lọc tra cứu lịch sử. */
    public List<RegistrationPeriod> getCouncilCreationPeriods() {
        return getAll().stream().filter(RegistrationPeriodService::canCreateCouncil).toList();
    }

    public static void requireStudentWindow(RegistrationPeriod period) {
        if (!isStudentWindow(period)) {
            throw new IllegalStateException("Đợt chưa mở hoặc đã hết thời hạn sinh viên đăng ký!");
        }
    }

    private final RegistrationPeriodRepository registrationPeriodRepository;

    public RegistrationPeriodService(RegistrationPeriodRepository registrationPeriodRepository) {
        this.registrationPeriodRepository = registrationPeriodRepository;
    }

    /** Ưu tiên đợt đang mở, sau đó sắp xếp đợt mới nhất trước. */
    public List<RegistrationPeriod> getAll() {
        return registrationPeriodRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing((RegistrationPeriod period) -> period.getStatus() != PeriodStatus.OPEN)
                        .thenComparing(RegistrationPeriod::getId, java.util.Comparator.reverseOrder()))
                .toList();
    }

    /** Tìm đợt đăng ký theo mã. */
    public RegistrationPeriod findById(Long id) {
        return registrationPeriodRepository.findById(id).orElse(null);
    }

    public List<RegistrationPeriod> getOpenStudentPeriods() {
        return registrationPeriodRepository.findAll().stream()
                .filter(RegistrationPeriodService::isStudentWindow)
                .sorted(java.util.Comparator.comparing(RegistrationPeriod::getStudentStartDate).reversed()
                        .thenComparing(RegistrationPeriod::getId, java.util.Comparator.reverseOrder()))
                .toList();
    }

    /** Lấy đợt đang mở hoặc được đánh dấu hoạt động. */
    public RegistrationPeriod getActivePeriod() {
        return registrationPeriodRepository.findAll().stream()
                .filter(p -> p.getStatus() == PeriodStatus.OPEN && p.isActive())
                .sorted(java.util.Comparator.comparing((RegistrationPeriod p) -> RegistrationPeriodService.isStudentWindow(p) ? 0 : 1)
                        .thenComparing(RegistrationPeriod::getStudentStartDate,
                                java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .findFirst()
                .orElse(null);
    }

    /** Thu thập toàn bộ lỗi để hiển thị ngay dưới trường tương ứng. */
    public Map<String, String> getValidationErrors(RegistrationPeriod period) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (period.getName() == null || period.getName().isBlank()) {
            errors.put("name", "Vui lòng nhập tên đợt đăng ký.");
        } else if (period.getName().trim().length() > 255) {
            errors.put("name", "Tên đợt đăng ký không được vượt quá 255 ký tự.");
        }
        if (period.getType() == null) errors.put("type", "Vui lòng chọn loại đợt đăng ký.");
        if (period.getStatus() == null) errors.put("status", "Vui lòng chọn trạng thái khởi tạo.");

        validateDate(errors, "lecturerStartDate", "ngày GV bắt đầu đề xuất", period.getLecturerStartDate(), true);
        validateDate(errors, "lecturerEndDate", "ngày GV kết thúc đề xuất", period.getLecturerEndDate(), true);
        validateDate(errors, "studentStartDate", "ngày SV bắt đầu đăng ký", period.getStudentStartDate(), true);
        validateDate(errors, "studentEndDate", "ngày SV kết thúc đăng ký", period.getStudentEndDate(), true);
        validateDate(errors, "councilReportDate", "ngày báo cáo hội đồng", period.getCouncilReportDate(),
                period.getType() == RegistrationType.KLTN);
        validateDate(errors, "reviewerDeadline", "hạn chót GVPB nộp điểm", period.getReviewerDeadline(),
                period.getType() == RegistrationType.KLTN || period.getType() == RegistrationType.TLCN);

        if (period.getLecturerStartDate() != null && period.getLecturerEndDate() != null
                && period.getLecturerEndDate().isBefore(period.getLecturerStartDate())) {
            errors.putIfAbsent("lecturerEndDate", "Ngày kết thúc đề xuất phải cùng hoặc sau ngày bắt đầu đề xuất.");
        }
        if (period.getStudentStartDate() != null && period.getStudentEndDate() != null
                && period.getStudentEndDate().isBefore(period.getStudentStartDate())) {
            errors.putIfAbsent("studentEndDate", "Ngày kết thúc đăng ký phải cùng hoặc sau ngày bắt đầu đăng ký.");
        }
        // TODO(TEST): Tạm comment các ràng buộc giữa các giai đoạn để test tất cả mốc cùng ngày.
        // Bỏ cặp /* ... */ bên dưới để bật lại kiểm tra thứ tự ngày.
        /*
        if (period.getLecturerEndDate() != null && period.getStudentStartDate() != null
                && !period.getStudentStartDate().isAfter(period.getLecturerEndDate())) {
            errors.putIfAbsent("studentStartDate", "Ngày SV bắt đầu đăng ký phải sau ngày GV kết thúc đề xuất.");
        }
        if (period.getCouncilReportDate() != null && period.getStudentEndDate() != null
                && !period.getCouncilReportDate().isAfter(period.getStudentEndDate())) {
            errors.putIfAbsent("councilReportDate", "Ngày báo cáo hội đồng phải sau ngày SV kết thúc đăng ký.");
        }
        if (period.getReviewerDeadline() != null && period.getStudentEndDate() != null
                && !period.getReviewerDeadline().isAfter(period.getStudentEndDate())) {
            errors.putIfAbsent("reviewerDeadline", "Hạn GVPB nộp điểm phải sau ngày SV kết thúc đăng ký.");
        }
        if (period.getReviewerDeadline() != null && period.getCouncilReportDate() != null
                && !period.getReviewerDeadline().isAfter(period.getCouncilReportDate())) {
            errors.putIfAbsent("reviewerDeadline", "Hạn GVPB nộp điểm phải sau ngày báo cáo hội đồng.");
        }
        */
        return errors;
    }

    private static void validateDate(Map<String, String> errors, String field, String label,
            LocalDate date, boolean required) {
        if (date == null && required) {
            errors.put(field, "Vui lòng nhập " + label + ".");
        } else if (date != null && (date.getYear() < 1000 || date.getYear() > 9999)) {
            errors.put(field, "Năm phải nằm trong khoảng 1000 đến 9999.");
        }
    }

    @Transactional
    /** Kiểm tra phía backend trước mọi thao tác lưu, kể cả khi gọi ngoài controller. */
    public RegistrationPeriod save(RegistrationPeriod period) {
        RegistrationPeriod target = period;
        if (period.getId() != null) {
            target = registrationPeriodRepository.findForUpdate(period.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt đăng ký."));
            // Kiểm tra ngày đang lưu trong CSDL, không dùng ngày mới do form gửi lên.
            if (!canEditPeriod(target))
                throw new IllegalStateException("Đợt đăng ký đã bắt đầu hoặc đã đóng, chỉ được xem và không thể chỉnh sửa.");
        }
        Map<String, String> errors = getValidationErrors(period);
        if (!errors.isEmpty()) throw new FormValidationException(errors);
        target.setName(period.getName().trim());
        target.setType(period.getType());
        target.setStatus(period.getStatus());
        target.setActive(period.getStatus() == PeriodStatus.OPEN);
        target.setLecturerStartDate(period.getLecturerStartDate());
        target.setLecturerEndDate(period.getLecturerEndDate());
        target.setStudentStartDate(period.getStudentStartDate());
        target.setStudentEndDate(period.getStudentEndDate());
        target.setCouncilReportDate(period.getCouncilReportDate());
        target.setReviewerDeadline(period.getReviewerDeadline());
        return registrationPeriodRepository.save(target);
    }

    @Transactional
    /** Xóa đợt đăng ký theo mã. */
    public void delete(Long id) {
        registrationPeriodRepository.deleteById(id);
    }
}
