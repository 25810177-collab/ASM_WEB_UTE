package ute.edu.dto;

import java.time.LocalDateTime;

public class SubmissionRow {
    private Long id;
    private Long reportId;
    private Long assignmentId;
    private String studentCode;
    private String studentName;
    private String classCode;
    private String topicTitle;
    private LocalDateTime submittedAt;
    private String fileName;
    private String fileUrl;
    private String githubUrl;
    private String demoUrl;
    private String note;
    private String gradeStatus;
    private Double score;
    private boolean late;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getDemoUrl() { return demoUrl; }
    public void setDemoUrl(String demoUrl) { this.demoUrl = demoUrl; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getGradeStatus() { return gradeStatus; }
    public void setGradeStatus(String gradeStatus) { this.gradeStatus = gradeStatus; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public boolean isLate() { return late; }
    public void setLate(boolean late) { this.late = late; }
}
