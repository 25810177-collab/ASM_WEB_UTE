package ute.edu.dto;

public class TopicRequest {
    private String title;
    private String description;
    private Long departmentId;
    private Long lecturerId;
    private Long coLecturerId;

    public TopicRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public Long getLecturerId() { return lecturerId; }
    public void setLecturerId(Long lecturerId) { this.lecturerId = lecturerId; }
    public Long getCoLecturerId() { return coLecturerId; }
    public void setCoLecturerId(Long coLecturerId) { this.coLecturerId = coLecturerId; }
}
