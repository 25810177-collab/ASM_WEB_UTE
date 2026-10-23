package ute.edu.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import ute.edu.enums.*;
import ute.edu.entity.*;
import ute.edu.repository.*;
import ute.edu.service.*;
import ute.edu.validation.FormValidationException;
import ute.edu.validation.CouncilForm;

import java.util.*;

import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
/**
 * Controller quản trị: quản lý đợt, đề tài, nhóm, hội đồng, điểm và thông báo.
 */
public class AdminController {
    private final TopicService topicService;
    private final RegistrationPeriodService periodService;
    private final DepartmentRepository departmentRepository;
    private final TopicRegistrationService registrationService;
    private final StudentGroupService groupService;
    private final StudentGroupRepository groupRepository;
    private final CouncilService councilService;
    private final ScoringService scoringService;
    private final ReportService reportService;
    private final NotificationService notificationService;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userRepository;
    private final TopicAssignmentRepository assignmentRepository;

    public AdminController(TopicService topicService,
            RegistrationPeriodService periodService,
            DepartmentRepository departmentRepository,
            TopicRegistrationService registrationService,
            StudentGroupService groupService,
            StudentGroupRepository groupRepository,
            CouncilService councilService,
            ScoringService scoringService,
            ReportService reportService,
            NotificationService notificationService,
            LectureRepository lectureRepository,
            StudentRepository studentRepository,
            UserAccountRepository userRepository,
            TopicAssignmentRepository assignmentRepository) {
        this.topicService = topicService;
        this.periodService = periodService;
        this.departmentRepository = departmentRepository;
        this.registrationService = registrationService;
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.councilService = councilService;
        this.scoringService = scoringService;
        this.reportService = reportService;
        this.notificationService = notificationService;
        this.lectureRepository = lectureRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // 1. Dashboard
    @GetMapping({ "", "/", "/dashboard" })
    /** Tổng hợp số liệu và các hoạt động gần đây cho trang quản trị. */
    public String dashboard(Model model) {
        List<Topic> topics = topicService.getAllTopics();
        model.addAttribute("topics", topics);
        List<RegistrationPeriod> periods = periodService.getAll();
        model.addAttribute("periods", periods);
        model.addAttribute("dashboardPeriods", periods.stream()
                .sorted(Comparator.comparing((RegistrationPeriod period) -> period.getId()).reversed())
                .limit(3)
                .toList());
        model.addAttribute("groupCount", groupRepository.count());
        model.addAttribute("councilCount", councilService.getAll().size());
        model.addAttribute("totalLecturers", lectureRepository.count());
        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("totalRegistrations", registrationService.getAll().size());

        // Count topics by status
        long publishedCount = topics.stream()
                .filter(t -> t.getStatus() == TopicStatus.PUBLISHED || t.getStatus() == TopicStatus.APPROVED).count();
        long pendingCount = topics.stream()
                .filter(t -> t.getStatus() == TopicStatus.PENDING || t.getStatus() == TopicStatus.DRAFT).count();
        long rejectedCount = topics.stream().filter(t -> t.getStatus() == TopicStatus.REJECTED).count();
        model.addAttribute("publishedTopicCount", publishedCount);
        model.addAttribute("pendingTopicCount", pendingCount);
        model.addAttribute("rejectedTopicCount", rejectedCount);

        model.addAttribute("recentRegistrations", registrationService.getAll().stream()
                .sorted(Comparator.comparing((TopicRegistration registration) -> registration.getId()).reversed())
                .limit(5)
                .toList());
        model.addAttribute("recentCouncils", councilService.getAll().stream()
                .sorted(Comparator.comparing((ReviewCouncil council) -> council.getId()).reversed())
                .limit(5)
                .toList());
        model.addAttribute("notifications", notificationService.getAll());

        return "admin/dashboard";
    }

    // 2. Registration Periods Management
    @GetMapping("/periods")
    /** Hiển thị danh sách và form quản lý đợt đăng ký. */
    public String registrationPeriods(Model model) {
        var periods = periodService.getAll();
        model.addAttribute("periods", periods);
        var editablePeriods = new HashMap<Long, Boolean>();
        periods.forEach(period -> editablePeriods.put(period.getId(), RegistrationPeriodService.canEditPeriod(period)));
        model.addAttribute("editablePeriods", editablePeriods);
        if (!model.containsAttribute("period")) {
            var period = new RegistrationPeriod();
            period.setType(RegistrationType.KLTN);
            period.setStatus(PeriodStatus.OPEN);
            model.addAttribute("period", period);
        }
        model.addAttribute("registrationTypes", RegistrationType.values());
        model.addAttribute("periodStatuses", PeriodStatus.values());
        return "admin/periods";
    }

    @InitBinder("period")
    public void bindPeriodFields(org.springframework.web.bind.WebDataBinder binder) {
        binder.setAllowedFields("id", "name", "type", "status", "lecturerStartDate", "lecturerEndDate",
                "studentStartDate", "studentEndDate", "councilReportDate", "reviewerDeadline");
    }

    @GetMapping("/periods/new")
    public String newPeriod(Model model) {
        model.addAttribute("periodFormOpen", true);
        return registrationPeriods(model);
    }

    @GetMapping("/periods/{id}")
    public String periodDetails(@PathVariable Long id, Model model) {
        RegistrationPeriod period = requirePeriod(id);
        model.addAttribute("period", period);
        preparePeriodEditor(period, model);
        return registrationPeriods(model);
    }

    private RegistrationPeriod requirePeriod(Long id) {
        RegistrationPeriod period = periodService.findById(id);
        if (period == null) throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy đợt đăng ký.");
        return period;
    }

    private void preparePeriodEditor(RegistrationPeriod period, Model model) {
        model.addAttribute("editingPeriodId", period.getId());
        model.addAttribute("periodReadOnly", !RegistrationPeriodService.canEditPeriod(period));
        model.addAttribute("periodFormOpen", true);
    }

    @PostMapping("/periods/save")
    /** Lưu một đợt đăng ký mới hoặc cập nhật đợt hiện có. */
    public String savePeriod(@ModelAttribute("period") RegistrationPeriod period,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        RegistrationPeriod current = period.getId() == null ? null : requirePeriod(period.getId());
        if (current != null && !RegistrationPeriodService.canEditPeriod(current)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đợt đăng ký đã bắt đầu hoặc đã đóng, chỉ được xem thông tin.");
            return "redirect:/admin/periods/" + current.getId();
        }
        periodService.getValidationErrors(period).forEach((field, message) -> {
            if (!bindingResult.hasFieldErrors(field)) bindingResult.rejectValue(field, "period.invalid", message);
        });
        if (!bindingResult.hasErrors()) {
            try {
                UserAccount adminUser = (UserAccount) session.getAttribute("user");
                if (period.getId() == null && adminUser != null) {
                    period.setCreatedBy(adminUser);
                }
                periodService.save(period);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Lưu đợt đăng ký '" + period.getName() + "' thành công!");
                return "redirect:/admin/periods";
            } catch (FormValidationException e) {
                e.getFieldErrors().forEach((field, message) -> bindingResult.rejectValue(field, "period.invalid", message));
            } catch (Exception e) {
                bindingResult.reject("period.saveFailed", ute.edu.helper.UserMessages.forException(e));
            }
        }
        if (current != null) {
            RegistrationPeriod latest = requirePeriod(current.getId());
            if (!RegistrationPeriodService.canEditPeriod(latest)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Đợt đăng ký đã bắt đầu hoặc đã đóng, chỉ được xem thông tin.");
                return "redirect:/admin/periods/" + latest.getId();
            }
            preparePeriodEditor(latest, model);
        }
        model.addAttribute("period", period);
        model.addAttribute("periodFormHasErrors", true);
        return registrationPeriods(model);
    }

    @PostMapping("/periods/{id}/delete")
    /** Xóa đợt đăng ký theo mã. */
    public String deletePeriod(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            periodService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa đợt đăng ký thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa đợt này do đang có đề tài hoặc dữ liệu liên kết.");
        }
        return "redirect:/admin/periods";
    }

    // 3. Duyệt đề xuất đề tài (GVHD nộp → Trưởng khoa chấp nhận/từ chối → công bố)
    @GetMapping("/topics")
    /** Lọc và hiển thị danh sách đề tài cho quản trị. */
    public String topics(@RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long periodId,
            Model model) {
        List<Topic> list = topicService.getAllTopics();
        if (departmentId != null && departmentId > 0) {
            list = list.stream()
                    .filter(t -> t.getDepartment() != null && t.getDepartment().getId().equals(departmentId)).toList();
        }
        if (periodId != null && periodId > 0) {
            list = list.stream().filter(
                    t -> t.getRegistrationPeriod() != null && t.getRegistrationPeriod().getId().equals(periodId))
                    .toList();
        }

        model.addAttribute("topics", list);
        model.addAttribute("topic", new Topic());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("periods", periodService.getAll());
        model.addAttribute("lecturers", lectureRepository.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "user.createdAt", "id")));
        model.addAttribute("statuses", TopicStatus.values());
        model.addAttribute("selectedDept", departmentId);
        model.addAttribute("selectedPeriod", periodId);
        return "admin/topics";
    }

    @PostMapping("/topics/save")
    /** Lưu thông tin đề tài. */
    public String saveTopic(@ModelAttribute Topic topic,
            @RequestParam(name = "departmentId") Long departmentId,
            @RequestParam(name = "periodId") Long periodId,
            @RequestParam(name = "primaryLecturerId") Long primaryLecturerId,
            @RequestParam(name = "coLecturerId", required = false) Long coLecturerId,
            RedirectAttributes redirectAttributes) {
        try {
            topic.setDepartment(departmentRepository.findById(departmentId).orElseThrow());
            topic.setRegistrationPeriod(periodService.findById(periodId));
            topic.setLecturer(lectureRepository.findById(primaryLecturerId).orElse(null));
            if (coLecturerId != null && coLecturerId > 0) {
                topic.setCoLecturer(lectureRepository.findById(coLecturerId).orElse(null));
            } else {
                topic.setCoLecturer(null);
            }
            topicService.save(topic);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin đề tài thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi lưu đề tài: " + ute.edu.helper.UserMessages.forException(e));
        }
        return "redirect:/admin/topics";
    }

    @PostMapping("/topics/{id}/status")
    /** Duyệt, từ chối hoặc đổi trạng thái đề tài. */
    public String updateTopicStatus(@PathVariable Long id,
            @RequestParam TopicStatus status,
            RedirectAttributes redirectAttributes) {
        try {
            topicService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đề tài thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + ute.edu.helper.UserMessages.forException(e));
        }
        return "redirect:/admin/topics";
    }

    @PostMapping(value = "/topics/{id}/status-ajax", produces = "application/json")
    @ResponseBody
    /** Đổi trạng thái đề tài và trả kết quả JSON. */
    public Map<String, Object> updateTopicStatusAjax(@PathVariable Long id,
            @RequestParam TopicStatus status,
            @RequestParam(required = false) String rejectionReason) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Topic updated = topicService.updateStatus(id, status);
            resp.put("success", true);
            resp.put("status", updated.getStatus().name());
            resp.put("message", status == TopicStatus.REJECTED
                    ? "Đã từ chối đề tài"
                            + (rejectionReason != null && !rejectionReason.isBlank() ? ": " + rejectionReason.trim()
                                    : ".")
                    : "Cập nhật trạng thái đề tài thành công!");
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", ute.edu.helper.UserMessages.forException(e));
        }
        return resp;
    }

    @PostMapping("/topics/{id}/delete")
    /** Xóa đề tài. */
    public String deleteTopic(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            topicService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa đề tài thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa đề tài đang có nhóm đăng ký hoặc liên kết.");
        }
        return "redirect:/admin/topics";
    }

    // 4. Student Groups Management
    @GetMapping("/groups")
    /** Hiển thị danh sách nhóm sinh viên. */
    public String groups(Model model) {
        List<StudentGroup> groups = groupService.getAll();
        model.addAttribute("groups", groups);
        model.addAttribute("periods", periodService.getAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/groups";
    }

    // 5. Review Councils & Assignment
    private static boolean sameId(Long a, Long b) {
        return a != null && a.equals(b);
    }

    @GetMapping("/councils")
    /** Hiển thị hội đồng và danh sách đề tài đủ điều kiện phân công. */
    public String councils(Model model) {
        List<ReviewCouncil> councils = councilService.getAll();
        List<TopicRegistration> approvedRegistrations = registrationService.getApprovedRegistrations();
        // Lọc 1 lần các đăng ký chưa phân công và đủ điều kiện, rồi mới so khớp từng hội đồng theo đợt/khoa
        // trước khi gọi canAssignToCouncil (tránh hàng trăm câu truy vấn lặp khi CSDL đặt ở máy chủ xa).
        java.util.Set<Long> assignedIds = assignmentRepository.findAssignedRegistrationIds();
        List<TopicRegistration> candidates = councilService.filterEligibleForCouncil(approvedRegistrations.stream()
                .filter(registration -> !assignedIds.contains(registration.getId()))
                .toList());
        Map<Long, List<TopicRegistration>> eligibleRegistrationsByCouncil = new HashMap<>();
        Map<Long, List<TopicAssignment>> assignmentsByCouncil = new HashMap<>();
        Map<Long, List<CouncilService.ReportTimeSlot>> reportTimeSlotsByCouncil = new HashMap<>();
        Map<Long, String> assignedCouncilPeriodsByLecturer = new HashMap<>();
        for (ReviewCouncil council : councils) {
            if (council.getRegistrationPeriod() != null
                    && !council.getId().equals(model.getAttribute("editingCouncilId"))) {
                String periodId = council.getRegistrationPeriod().getId().toString();
                for (ReviewCouncilMember member : council.getMembers()) {
                    assignedCouncilPeriodsByLecturer.merge(member.getLecturer().getId(), periodId,
                            (existing, value) -> existing + "," + value);
                }
            }
            List<TopicAssignment> assignments = councilService.getAssignments(council.getId()).stream()
                    .sorted(java.util.Comparator.comparing(TopicAssignment::getStartTime,
                            java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                    .toList();
            assignmentsByCouncil.put(council.getId(), assignments);
            reportTimeSlotsByCouncil.put(council.getId(), councilService.getReportTimeSlots(assignments));
            List<TopicRegistration> eligibleRegistrations = candidates.stream()
                    .filter(registration -> sameId(council.getRegistrationPeriod() == null ? null : council.getRegistrationPeriod().getId(),
                            registration.getTopic().getRegistrationPeriod() == null ? null : registration.getTopic().getRegistrationPeriod().getId())
                            && sameId(council.getDepartment() == null ? null : council.getDepartment().getId(),
                                    registration.getTopic().getDepartment() == null ? null : registration.getTopic().getDepartment().getId()))
                    .filter(registration -> councilService.canAssignToCouncil(council, registration))
                    .toList();
            eligibleRegistrationsByCouncil.put(council.getId(), eligibleRegistrations);
        }

        model.addAttribute("councils", councils);
        model.addAttribute("periods", periodService.getAll());
        model.addAttribute("departments", departmentRepository.findAll());
        var councilLecturers = new LinkedHashMap<Long, Lecture>();
        councilService.getCouncilCandidates().forEach(lecturer -> councilLecturers.put(lecturer.getId(), lecturer));
        if (model.getAttribute("selectedCouncil") instanceof ReviewCouncil selected)
            selected.getMembers().forEach(member -> councilLecturers.putIfAbsent(member.getLecturer().getId(), member.getLecturer()));
        model.addAttribute("lecturers", new ArrayList<>(councilLecturers.values()));
        model.addAttribute("eligibleRegistrationsByCouncil", eligibleRegistrationsByCouncil);
        model.addAttribute("assignmentsByCouncil", assignmentsByCouncil);
        model.addAttribute("reportTimeSlotsByCouncil", reportTimeSlotsByCouncil);
        model.addAttribute("assignedCouncilPeriodsByLecturer", assignedCouncilPeriodsByLecturer);
        model.addAttribute("councilCreationPeriods", periodService.getCouncilCreationPeriods());
        if (!model.containsAttribute("councilForm")) model.addAttribute("councilForm", new CouncilForm());
        return "admin/councils";
    }

    @GetMapping("/councils/new")
    public String newCouncil(Model model) {
        model.addAttribute("councilFormOpen", true);
        return councils(model);
    }

    @GetMapping("/councils/{id}")
    public String councilDetails(@PathVariable Long id, Model model) {
        ReviewCouncil council = requireCouncil(id);
        model.addAttribute("councilForm", CouncilForm.fromCouncil(council));
        prepareCouncilEditor(council, model);
        return councils(model);
    }

    private ReviewCouncil requireCouncil(Long id) {
        ReviewCouncil council = councilService.findById(id);
        if (council == null) throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy hội đồng.");
        return council;
    }

    private void prepareCouncilEditor(ReviewCouncil council, Model model) {
        model.addAttribute("selectedCouncil", council);
        model.addAttribute("editingCouncilId", council.getId());
        model.addAttribute("councilReadOnly", !councilService.getAssignments(council.getId()).isEmpty());
        model.addAttribute("councilFormOpen", true);
        if (!RegistrationPeriodService.canCreateCouncil(council.getRegistrationPeriod()))
            model.addAttribute("unavailableCouncilPeriod", council.getRegistrationPeriod());
    }

    @PostMapping("/councils/{id}/update")
    public String updateCouncil(@PathVariable Long id,
            @ModelAttribute("councilForm") CouncilForm form, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
        ReviewCouncil current = requireCouncil(id);
        if (!councilService.getAssignments(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hội đồng đã phân công đề tài, chỉ được xem thông tin.");
            return "redirect:/admin/councils/" + id;
        }
        var changes = new ReviewCouncil();
        changes.setCode(form.getCode());
        changes.setName(form.getName());
        changes.setLocation(form.getLocation());
        changes.setRegistrationPeriod(form.getPeriodId() == null ? null : periodService.findById(form.getPeriodId()));
        changes.setDepartment(form.getDepartmentId() == null ? null
                : departmentRepository.findById(form.getDepartmentId()).orElse(null));
        councilService.getUpdateValidationErrors(id, changes, form.getChairmanId(), form.getSecretaryId(), form.getMemberIds())
                .forEach((field, message) -> {
                    if (!bindingResult.hasFieldErrors(field)) bindingResult.rejectValue(field, "council.invalid", message);
                });
        if (!bindingResult.hasErrors()) {
            try {
                councilService.updateCouncil(id, changes, form.getChairmanId(), form.getSecretaryId(), form.getMemberIds());
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hội đồng thành công!");
                return "redirect:/admin/councils";
            } catch (FormValidationException e) {
                e.getFieldErrors().forEach((field, message) -> bindingResult.rejectValue(field, "council.invalid", message));
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                bindingResult.rejectValue("code", "council.duplicateCode", "Không thể lưu hội đồng. Vui lòng kiểm tra mã hội đồng có bị trùng hay không.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                bindingResult.reject("council.saveFailed", ute.edu.helper.UserMessages.forException(e));
            }
        }
        if (!councilService.getAssignments(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hội đồng vừa được phân công đề tài, chỉ được xem thông tin.");
            return "redirect:/admin/councils/" + id;
        }
        model.addAttribute("councilFormHasErrors", true);
        prepareCouncilEditor(current, model);
        // Giữ đợt người dùng vừa chọn khi dữ liệu không hợp lệ.
        if (!RegistrationPeriodService.canCreateCouncil(changes.getRegistrationPeriod()))
            model.addAttribute("unavailableCouncilPeriod", changes.getRegistrationPeriod());
        return councils(model);
    }

    @PostMapping("/councils/save")
    /** Tạo hội đồng và thiết lập thành viên. */
    public String saveCouncil(@ModelAttribute("councilForm") CouncilForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var council = new ReviewCouncil();
        council.setCode(form.getCode());
        council.setName(form.getName());
        council.setLocation(form.getLocation());
        council.setRegistrationPeriod(form.getPeriodId() == null ? null : periodService.findById(form.getPeriodId()));
        council.setDepartment(form.getDepartmentId() == null ? null
                : departmentRepository.findById(form.getDepartmentId()).orElse(null));
        councilService.getCreationValidationErrors(council, form.getChairmanId(), form.getSecretaryId(), form.getMemberIds())
                .forEach((field, message) -> {
                    if (!bindingResult.hasFieldErrors(field)) bindingResult.rejectValue(field, "council.invalid", message);
                });
        if (!bindingResult.hasErrors()) {
            try {
                councilService.createCouncil(council, form.getChairmanId(), form.getSecretaryId(), form.getMemberIds());
                redirectAttributes.addFlashAttribute("successMessage", "Thành lập Hội đồng phản biện thành công!");
                return "redirect:/admin/councils";
            } catch (FormValidationException e) {
                e.getFieldErrors().forEach((field, message) -> bindingResult.rejectValue(field, "council.invalid", message));
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                bindingResult.rejectValue("code", "council.duplicateCode", "Không thể lưu hội đồng. Vui lòng kiểm tra mã hội đồng có bị trùng hay không.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                bindingResult.reject("council.saveFailed", ute.edu.helper.UserMessages.forException(e));
            }
        }
        model.addAttribute("councilFormHasErrors", true);
        if (!RegistrationPeriodService.canCreateCouncil(council.getRegistrationPeriod()))
            model.addAttribute("unavailableCouncilPeriod", council.getRegistrationPeriod());
        return councils(model);
    }

    @PostMapping("/councils/{id}/assign-topic")
    /** Phân công đề tài vào hội đồng. */
    public String assignTopicToCouncil(@PathVariable Long id,
            @RequestParam Long registrationId,
            @RequestParam(required = false) String startTime,
            RedirectAttributes redirectAttributes) {
        try {
            councilService.assignTopic(id, registrationId, parseReportTime(startTime));
            redirectAttributes.addFlashAttribute("successMessage", "Đã phân công đề tài vào Hội đồng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi phân công: " + ute.edu.helper.UserMessages.forException(e));
        }
        return "redirect:/admin/councils";
    }

    private java.time.LocalTime parseReportTime(String startTime) {
        try {
            return java.time.LocalTime.parse(startTime == null ? "" : startTime);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Vui lòng chọn giờ báo cáo hợp lệ!");
        }
    }

    @PostMapping("/councils/{id}/delete")
    /** Xóa hội đồng và các phân công liên quan. */
    public String deleteCouncil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            councilService.deleteCouncil(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa hội đồng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + ute.edu.helper.UserMessages.forException(e));
        }
        return "redirect:/admin/councils";
    }

    // 7. Scoring, Results & Publication
    @GetMapping("/results")
    /** Hiển thị kết quả chấm điểm của các hội đồng. */
    public String results(Model model) {
        List<ReviewCouncil> councils = councilService.getAll().stream()
                .filter(c -> c.getStatus() == CouncilStatus.COMPLETED
                        || !councilService.getAssignments(c.getId()).isEmpty())
                .toList();
        model.addAttribute("councils", councils);
        model.addAttribute("scoringService", scoringService);
        model.addAttribute("councilService", councilService);
        return "admin/results";
    }

    @PostMapping("/results/{councilId}/finalize")
    /** Chốt hội đồng sau khi đã đủ điểm. */
    public String finalizeCouncil(@PathVariable Long councilId,
            RedirectAttributes redirectAttributes) {
        try {
            scoringService.finalizeCouncil(councilId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã chốt và công bố điểm trung bình hội đồng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + ute.edu.helper.UserMessages.forException(e));
        }
        return "redirect:/admin/results";
    }

    // 8. Reports Management
    @GetMapping("/reports")
    /** Hiển thị báo cáo thuộc các đề tài đủ điều kiện. */
    public String reports(Model model) {
        model.addAttribute("reports", reportService.getAll().stream()
                .filter(report -> councilService.isEligibleForCouncil(report.getTopicRegistration()))
                .toList());
        model.addAttribute("periods", periodService.getAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/reports";
    }

    // 9. Notifications Management
    @GetMapping("/notifications")
    /** Hiển thị danh sách thông báo. */
    public String notifications(@RequestParam(required = false) Long notificationId,
            HttpSession session, Model model) {
        if (notificationId != null) {
            notificationService.markAsRead(notificationId, (UserAccount) session.getAttribute("user"));
            GlobalModelAdvice.refreshNotificationModel(notificationService, (UserAccount) session.getAttribute("user"),
                    model);
        }
        model.addAttribute("notifications", notificationService.getAll());
        model.addAttribute("types", NotificationType.values());
        return "admin/notifications";
    }

    @PostMapping("/notifications/save")
    /** Tạo thông báo mới. */
    public String saveNotification(@RequestParam String title,
            @RequestParam String content,
            @RequestParam NotificationType type,
            @RequestParam(defaultValue = "true") boolean published,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            UserAccount creator = (UserAccount) session.getAttribute("user");
            notificationService.create(title, content, type, published, creator);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng thông báo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi đăng thông báo: " + ute.edu.helper.UserMessages.forException(e));
        }
        return "redirect:/admin/notifications";
    }

    @PostMapping("/notifications/{id}/toggle")
    /** Bật hoặc tắt công bố thông báo. */
    public String toggleNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.togglePublish(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái thông báo.");
        return "redirect:/admin/notifications";
    }

    @PostMapping("/notifications/{id}/delete")
    /** Xóa thông báo. */
    public String deleteNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thông báo.");
        return "redirect:/admin/notifications";
    }

    // 10. User Management
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "createdAt", "id")));
        model.addAttribute("lecturers", lectureRepository.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "user.createdAt", "id")));
        model.addAttribute("students", studentRepository.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "user.createdAt", "id")));
        return "admin/users";
    }
}
