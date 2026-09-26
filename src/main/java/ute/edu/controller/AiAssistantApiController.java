package ute.edu.controller;

import jakarta.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ute.edu.entity.*;
import ute.edu.enums.RegistrationStatus;
import ute.edu.enums.TopicStatus;
import ute.edu.enums.UserRole;
import ute.edu.repository.*;
import ute.edu.service.*;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantApiController {

    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final UserAccountRepository userAccountRepository;
    private final TopicRepository topicRepository;
    private final StudentGroupService groupService;
    private final TopicRegistrationService registrationService;
    private final ReportService reportService;
    private final CouncilService councilService;
    private final ScoringService scoringService;
    private final RegistrationPeriodService periodService;
    private final TopicAssignmentRepository assignmentRepository;
    private final ScoreRepository scoreRepository;

    public AiAssistantApiController(StudentRepository studentRepository,
            LectureRepository lectureRepository,
            UserAccountRepository userAccountRepository,
            TopicRepository topicRepository,
            StudentGroupService groupService,
            TopicRegistrationService registrationService,
            ReportService reportService,
            CouncilService councilService,
            ScoringService scoringService,
            RegistrationPeriodService periodService,
            TopicAssignmentRepository assignmentRepository,
            ScoreRepository scoreRepository) {
        this.studentRepository = studentRepository;
        this.lectureRepository = lectureRepository;
        this.userAccountRepository = userAccountRepository;
        this.topicRepository = topicRepository;
        this.groupService = groupService;
        this.registrationService = registrationService;
        this.reportService = reportService;
        this.councilService = councilService;
        this.scoringService = scoringService;
        this.periodService = periodService;
        this.assignmentRepository = assignmentRepository;
        this.scoreRepository = scoreRepository;
    }

    private UserAccount getCurrentUser(HttpSession session) {
        return (UserAccount) session.getAttribute("user");
    }

    /**
     * Lấy ngữ cảnh dữ liệu thật theo role hiện tại để AI nắm bắt ngay lập tức.
     */
    @GetMapping("/context")
    public ResponseEntity<Map<String, Object>> getRealtimeContext(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        UserAccount user = getCurrentUser(session);
        RegistrationPeriod activePeriod = periodService.getActivePeriod();

        result.put("activePeriod", activePeriod != null ? activePeriod.getName() : "Chưa có đợt mở");
        result.put("activePeriodStatus", activePeriod != null ? activePeriod.getStatus().name() : "INACTIVE");

        if (user == null) {
            result.put("role", "GUEST");
            result.put("userName", "Khách");
            result.put("totalTopics", topicRepository.count());
            return ResponseEntity.ok(result);
        }

        result.put("role", user.getRole().name());
        result.put("userName", user.getFullName());
        result.put("userCode", user.getCode());

        if (user.getRole() == UserRole.STUDENT) {
            Student student = studentRepository.findByUserId(user.getId());
            if (student != null) {
                StudentGroup group = groupService.findGroupByStudent(student.getId());
                if (group != null) {
                    result.put("hasGroup", true);
                    result.put("groupName", group.getName());
                    result.put("isLeader",
                            group.getLeader() != null && group.getLeader().getId().equals(student.getId()));
                    result.put("memberCount", group.getMembers() != null ? group.getMembers().size() : 0);

                    TopicRegistration reg = registrationService.getActiveRegistrationForGroup(group.getId());
                    if (reg != null) {
                        result.put("hasRegistration", true);
                        result.put("regStatus", reg.getStatus().name());
                        result.put("topicName", reg.getTopic().getTitle());
                        result.put("topicCode", reg.getTopic().getCode());
                    } else {
                        result.put("hasRegistration", false);
                    }
                } else {
                    result.put("hasGroup", false);
                }
            }
        } else if (user.getRole() == UserRole.LECTURER) {
            Lecture lecturer = lectureRepository.findByUserId(user.getId());
            if (lecturer != null) {
                List<Topic> topics = topicRepository.findByLecturerIdOrCoLecturerId(lecturer.getId(), lecturer.getId());
                result.put("proposedTopicsCount", topics.size());
                List<ReviewCouncilMember> councils = councilService.getCouncilsForLecturer(lecturer.getId());
                result.put("councilDutiesCount", councils.size());
            }
        } else if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.DEAN
                || user.getRole() == UserRole.DEPARTMENT_HEAD) {
            result.put("totalUsers", userAccountRepository.count());
            result.put("totalTopics", topicRepository.count());
            result.put("totalCouncils", councilService.getAll().size());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * API thực thi câu hỏi từ người dùng bằng cách TRUY VẤN DỮ LIỆU THẬT trong
     * Database.
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> handleAiQuery(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "path", required = false) String path,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        UserAccount user = getCurrentUser(session);
        String q = (query != null ? query : "").trim().toLowerCase();

        String answer = processQueryWithRealData(q, user, path);

        response.put("success", true);
        response.put("answer", answer);
        response.put("role", user != null ? user.getRole().name() : "GUEST");
        return ResponseEntity.ok(response);
    }

    private String normalize(String input) {
        if (input == null)
            return "";
        String n = input.toLowerCase().trim();
        n = java.text.Normalizer.normalize(n, java.text.Normalizer.Form.NFD);
        n = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(n).replaceAll("");
        n = n.replace('đ', 'd').replace('Đ', 'd');
        return n;
    }

    private String processQueryWithRealData(String q, UserAccount user, String path) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        RegistrationPeriod activePeriod = periodService.getActivePeriod();
        String qNorm = normalize(q);

        // 1. SINH VIÊN: Truy vấn dữ liệu thật của bản thân
        if (user != null && user.getRole() == UserRole.STUDENT) {
            Student student = studentRepository.findByUserId(user.getId());
            if (student == null) {
                return "Không tìm thấy hồ sơ sinh viên liên kết với tài khoản này.";
            }

            StudentGroup group = groupService.findGroupByStudent(student.getId());

            // A. Hỏi về Điểm / Nhận xét Hội đồng
            if (qNorm.contains("diem") || qNorm.contains("ket qua") || qNorm.contains("nhan xet")
                    || qNorm.contains("hoi dong")) {
                if (group == null) {
                    return "Bạn hiện chưa tham gia nhóm sinh viên nào nên chưa có thông tin bảo vệ đồ án.";
                }
                TopicRegistration reg = registrationService.getActiveRegistrationForGroup(group.getId());
                if (reg == null || reg.getStatus() != RegistrationStatus.APPROVED) {
                    return "Nhóm **" + group.getName()
                            + "** chưa có đề tài được duyệt chính thức nên chưa bước vào giai đoạn chấm điểm hội đồng.";
                }

                TopicAssignment assignment = assignmentRepository.findFirstByTopicRegistrationId(reg.getId())
                        .orElse(null);
                if (assignment == null) {
                    return "Đề tài **" + reg.getTopic().getTitle()
                            + "** của nhóm bạn đang thực hiện, hiện Khoa chưa phân công Hội đồng chấm bảo vệ.";
                }

                List<Score> scores = scoreRepository.findByTopicAssignmentId(assignment.getId());
                if (scores.isEmpty()) {
                    ReviewCouncil council = assignment.getCouncil();
                    String councilName = council != null ? council.getName() : "Hội đồng đánh giá";
                    return "🎯 **Hội đồng phân công:** " + councilName + "\n\n" +
                            "⏳ **Trạng thái:** Hội đồng đang trong quá trình chấm điểm và ghi nhận xét. Kết quả sẽ tự động hiển thị ngay khi phiên bảo vệ hoàn tất!";
                }

                StringBuilder sb = new StringBuilder();
                sb.append("🎯 **Kết quả & Nhận xét thực tế từ Hội đồng bảo vệ:**\n\n");
                double sum = 0;
                for (Score s : scores) {
                    ReviewCouncilMember m = s.getCouncilMember();
                    String lecturerName = (m != null && m.getLecturer() != null && m.getLecturer().getUser() != null)
                            ? m.getLecturer().getUser().getFullName()
                            : "Giảng viên";
                    String role = m != null && m.getRole() != null ? m.getRole().name() : "Thành viên";
                    double val = s.getScore() != null ? s.getScore() : 0.0;
                    sum += val;

                    sb.append("👤 **").append(lecturerName).append("** (").append(role).append("):\n");
                    sb.append("  - **Điểm:** `").append(val).append(" / 10`\n");
                    sb.append("  - **Nhận xét:** *\"")
                            .append(s.getComment() != null ? s.getComment() : "Không có nhận xét").append("\"*\n\n");
                }
                double avg = Math.round((sum / scores.size()) * 10.0) / 10.0;
                sb.append("🏆 **Điểm trung bình tổng kết:** `").append(avg).append(" / 10` (").append(scores.size())
                        .append(" Giảng viên đã chấm).");
                return sb.toString();
            }

            // B. Hỏi về Nhóm / Thành viên
            if (qNorm.contains("nhom") || qNorm.contains("thanh vien") || qNorm.contains("ban")) {
                if (group == null) {
                    return "Bạn hiện **chưa có nhóm**. Hãy vào mục **Nhóm của tôi** (`/student/group`) để tạo nhóm mới hoặc nhập mã để tham gia nhóm của bạn bè!";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("👥 **Thông tin nhóm thật của bạn:**\n");
                sb.append("- **Tên nhóm:** ").append(group.getName()).append("\n");
                String leaderName = group.getLeader() != null && group.getLeader().getUser() != null
                        ? group.getLeader().getUser().getFullName()
                        : "Chưa rõ";
                sb.append("- **Nhóm trưởng:** ").append(leaderName).append("\n");
                sb.append("- **Trạng thái nhóm:** `").append(group.getStatus()).append("`\n\n");
                sb.append("📋 **Danh sách thành viên:**\n");

                if (group.getMembers() != null && !group.getMembers().isEmpty()) {
                    for (StudentGroupMember m : group.getMembers()) {
                        Student s = m.getStudent();
                        if (s != null && s.getUser() != null) {
                            sb.append("  • ").append(s.getUser().getFullName())
                                    .append(" (MSSV: `").append(s.getUser().getCode()).append("`)")
                                    .append(m.isLeader() ? " ⭐ *[Trưởng nhóm]*" : "")
                                    .append("\n");
                        }
                    }
                } else {
                    sb.append("  • Chưa có thành viên nào khác.\n");
                }
                return sb.toString();
            }

            // C. Hỏi về Đề tài đã đăng ký / GVHD
            if (qNorm.contains("de tai") || qNorm.contains("gvhd") || qNorm.contains("huong dan")
                    || qNorm.contains("dang ky")) {
                if (group == null) {
                    return "Bạn cần lập nhóm trước khi đăng ký đề tài đồ án!";
                }
                TopicRegistration reg = registrationService.getActiveRegistrationForGroup(group.getId());
                if (reg == null) {
                    return "Nhóm **" + group.getName()
                            + "** hiện chưa đăng ký đề tài nào. Hãy truy cập **Danh mục đề tài** (`/student/topics`) để lựa chọn đề tài phù hợp!";
                }
                Topic t = reg.getTopic();
                StringBuilder sb = new StringBuilder();
                sb.append("📚 **Đề tài nhóm bạn đã đăng ký:**\n");
                sb.append("- **Tên đề tài:** ").append(t.getTitle()).append("\n");
                sb.append("- **Mã đề tài:** `").append(t.getCode()).append("`\n");
                sb.append("- **Trạng thái duyệt:** `").append(reg.getStatus()).append("`\n");

                String gvhd = (t.getLecturer() != null && t.getLecturer().getUser() != null)
                        ? t.getLecturer().getUser().getFullName()
                        : "Chưa chỉ định";
                sb.append("- **GVHD chính:** ").append(gvhd).append("\n");
                if (t.getCoLecturer() != null && t.getCoLecturer().getUser() != null) {
                    sb.append("- **Đồng hướng dẫn:** ").append(t.getCoLecturer().getUser().getFullName()).append("\n");
                }
                return sb.toString();
            }

            // D. Hỏi về Báo cáo tiến độ
            if (qNorm.contains("bao cao") || qNorm.contains("tien do") || qNorm.contains("nop")) {
                if (group == null) {
                    return "Bạn chưa tham gia nhóm nào để nộp báo cáo.";
                }
                TopicRegistration reg = registrationService.getActiveRegistrationForGroup(group.getId());
                if (reg == null) {
                    return "Nhóm **" + group.getName() + "** chưa đăng ký đề tài nên chưa có đợt nộp báo cáo.";
                }
                List<Report> reports = reportService.getReportsForRegistration(reg.getId());
                if (reports.isEmpty()) {
                    return "Nhóm **" + group.getName()
                            + "** chưa nộp báo cáo tiến độ nào. Hãy truy cập **Báo cáo tiến độ** (`/student/reports`) để nộp báo cáo theo yêu cầu của GVHD.";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("📑 **Tiến độ báo cáo thực tế:** Nhóm đã nộp **").append(reports.size())
                        .append(" báo cáo**.\n\n");
                Report latest = reports.get(0);
                sb.append("- **Tập tin gần nhất:** ").append(latest.getFileName()).append("\n");
                sb.append("- **Thời gian nộp:** ")
                        .append(latest.getSubmittedAt() != null ? latest.getSubmittedAt().format(dtf) : "Vừa xong")
                        .append("\n");
                sb.append("- **Trạng thái duyệt:** `").append(latest.getReviewStatus()).append("`\n");
                if (latest.getNote() != null && !latest.getNote().isEmpty()) {
                    sb.append("- **Ghi chú của SV:** *\"").append(latest.getNote()).append("\"*\n");
                }
                return sb.toString();
            }
        }

        // 2. GIẢNG VIÊN: Truy vấn dữ liệu thực tế
        if (user != null && user.getRole() == UserRole.LECTURER) {
            Lecture lecturer = lectureRepository.findByUserId(user.getId());
            if (lecturer == null)
                return "Không tìm thấy hồ sơ giảng viên của bạn.";

            if (qNorm.contains("de tai") || qNorm.contains("de xuat")) {
                List<Topic> myTopics = topicRepository.findByLecturerIdOrCoLecturerId(lecturer.getId(),
                        lecturer.getId());
                long approved = myTopics.stream().filter(t -> t.getStatus() == TopicStatus.APPROVED).count();
                long pending = myTopics.stream().filter(t -> t.getStatus() == TopicStatus.PENDING).count();

                StringBuilder sb = new StringBuilder();
                sb.append("👨‍🏫 **Dữ liệu đề tài của Thầy/Cô:**\n");
                sb.append("- **Tổng số đề tài đề xuất:** `").append(myTopics.size()).append("` đề tài\n");
                sb.append("- **Đã duyệt (Công bố):** `").append(approved).append("`\n");
                sb.append("- **Chờ thẩm định:** `").append(pending).append("`\n\n");
                if (!myTopics.isEmpty()) {
                    sb.append("📋 **Top đề tài gần nhất:**\n");
                    myTopics.stream().limit(3).forEach(
                            t -> sb.append("  • [").append(t.getCode()).append("] ").append(t.getTitle()).append("\n"));
                }
                return sb.toString();
            }

            if (qNorm.contains("hoi dong") || qNorm.contains("cham") || qNorm.contains("lich")) {
                List<ReviewCouncilMember> councils = councilService.getCouncilsForLecturer(lecturer.getId());
                if (councils.isEmpty()) {
                    return "Thầy/Cô hiện chưa được phân công tham gia Hội đồng bảo vệ nào trong đợt này.";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("⚖️ **Nhiệm vụ Hội đồng bảo vệ thực tế (").append(councils.size())
                        .append(" hội đồng):**\n\n");
                for (ReviewCouncilMember cm : councils) {
                    ReviewCouncil c = cm.getCouncil();
                    sb.append("- **").append(c.getName()).append("**\n");
                    sb.append("  • Vai trò: `").append(cm.getRole()).append("`\n");
                    sb.append("  • Địa điểm/Phòng: ")
                            .append(c.getLocation() != null ? c.getLocation() : "Chưa xếp phòng").append("\n");
                    sb.append("  • Ngày bảo vệ: ")
                            .append(c.getCouncilDate() != null ? c.getCouncilDate().format(dtf) : "Đang cập nhật")
                            .append("\n\n");
                }
                return sb.toString();
            }
        }

        // 3. Thống kê thực tế hệ thống
        if (qNorm.contains("thong ke") || qNorm.contains("so lieu") || qNorm.contains("tong quan")) {
            long totalUsers = userAccountRepository.count();
            long totalTopics = topicRepository.count();
            long totalCouncils = councilService.getAll().size();

            return "📊 **Thống kê thực tế toàn hệ thống:**\n\n" +
                    "- 👥 **Tổng người dùng:** `" + totalUsers + "` tài khoản.\n" +
                    "- 📚 **Tổng danh mục đề tài:** `" + totalTopics + "` đề tài.\n" +
                    "- 🏛️ **Số lượng Hội đồng bảo vệ:** `" + totalCouncils + "` hội đồng.\n" +
                    "- ⏳ **Đợt đăng ký hiện hành:** " + (activePeriod != null ? activePeriod.getName() : "Không có");
        }

        // 4. Tra cứu Đợt đăng ký (Mọi đối tượng)
        if (qNorm.contains("dot") || qNorm.contains("han") || qNorm.contains("thoi gian")
                || qNorm.contains("deadline")) {
            if (activePeriod == null) {
                return "Hiện tại Khoa **chưa mở đợt đăng ký nào** hoặc đợt đăng ký đã kết thúc.";
            }
            return "⏳ **Thông tin Đợt đăng ký hiện hành:**\n\n" +
                    "- **Tên đợt:** " + activePeriod.getName() + "\n" +
                    "- **Thời gian SV đăng ký:** "
                    + (activePeriod.getStudentStartDate() != null ? activePeriod.getStudentStartDate().format(dtf)
                            : "---")
                    +
                    " đến "
                    + (activePeriod.getStudentEndDate() != null ? activePeriod.getStudentEndDate().format(dtf) : "---")
                    + "\n" +
                    "- **Hạn phản biện chấm điểm:** "
                    + (activePeriod.getReviewerDeadline() != null ? activePeriod.getReviewerDeadline().format(dtf)
                            : "---")
                    + "\n" +
                    "- **Trạng thái:** `" + activePeriod.getStatus() + "`";
        }

        // 5. Tra cứu đề tài theo từ khóa thực tế
        if (qNorm.startsWith("tim ") || qNorm.startsWith("tra cuu ") || qNorm.contains("de tai ai")
                || qNorm.contains("de tai web") || qNorm.contains("de tai iot")) {
            String keyword = qNorm.replace("tim ", "").replace("tra cuu ", "").replace("de tai ", "").trim();
            List<Topic> all = topicRepository.findAll();
            List<Topic> matches = all.stream()
                    .filter(t -> normalize(t.getTitle()).contains(keyword)
                            || (t.getCode() != null && normalize(t.getCode()).contains(keyword)))
                    .limit(5)
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                return "Không tìm thấy đề tài nào khớp với từ khóa **\"" + keyword
                        + "\"**. Bạn hãy thử tìm với các chuyên ngành như *AI, Web, IoT, An ninh mạng* nhé!";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("🔍 **Tìm thấy ").append(matches.size()).append(" đề tài thật trong cơ sở dữ liệu:**\n\n");
            for (Topic t : matches) {
                sb.append("• **[").append(t.getCode()).append("]** ").append(t.getTitle()).append("\n");
                sb.append("  - Trạng thái: `").append(t.getStatus()).append("` | Tối đa: ").append(t.getMaxStudents())
                        .append(" SV\n");
            }
            return sb.toString();
        }

        // 6. Quy trình 6 bước chuẩn
        if (qNorm.contains("quy trinh") || qNorm.contains("buoc") || qNorm.contains("huong dan")) {
            return "🎓 **Quy trình chuẩn 6 bước Quản lý Đồ án HCMUTE:**\n\n" +
                    "1. **Mở đợt & Đề xuất:** Khoa thông báo kế hoạch; Giảng viên đề xuất danh mục đề tài.\n" +
                    "2. **Lập nhóm:** Sinh viên lập nhóm từ 1 - 3 thành viên.\n" +
                    "3. **Đăng ký đề tài:** Nhóm trưởng gửi đơn; GVHD xét duyệt.\n" +
                    "4. **Thực hiện & Báo cáo:** Sinh viên nộp báo cáo tiến độ tuần theo quy định.\n" +
                    "5. **Sơ duyệt & Lập hội đồng:** GVHD duyệt đủ điều kiện; Khoa thành lập Hội đồng bảo vệ.\n" +
                    "6. **Bảo vệ & Công bố điểm:** Hội đồng chấm điểm thang 10 độc lập và ghi nhận xét.";
        }

        // 7. Mặc định
        return "Chào bạn! Tôi đã kết nối trực tiếp với **Cơ sở dữ liệu thực tế của hệ thống**.\n\n" +
                "Bạn có thể hỏi tôi:\n" +
                "- **\"Thông tin nhóm của tôi\"** (xem thành viên, nhóm trưởng)\n" +
                "- **\"Xem điểm và nhận xét hội đồng\"** (đọc điểm thật từ CSDL)\n" +
                "- **\"Đề tài đã đăng ký\"** hoặc **\"Tiến độ nộp báo cáo\"**\n" +
                "- **\"Thời hạn đợt đăng ký\"** hoặc **\"Tìm đề tài AI / Web\"**";
    }
}
