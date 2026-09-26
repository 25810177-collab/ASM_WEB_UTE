package ute.edu.dto;

import java.time.LocalDateTime;

public class SubmissionHistoryItem {
    private Long id;
    private int version;
    private LocalDateTime submittedAt;
    private String fileName;
    private String fileUrl;
    private String githubUrl;
    private String demoUrl;
    private String note;
    private boolean late;
    private String statusLabel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
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
    public boolean isLate() { return late; }
    public void setLate(boolean late) { this.late = late; }
    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }
}
