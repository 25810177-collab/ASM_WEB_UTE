package ute.edu.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ute.edu.enums.TopicStatus;

@Entity
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private Lecture lecturer;

    @ManyToOne
    @JoinColumn(name = "co_lecturer_id")
    private Lecture coLecturer;

    @Column(nullable = false)
    private int maxStudents = 3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicStatus status = TopicStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Topic() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Lecture getLecturer() { return lecturer; }
    public void setLecturer(Lecture lecturer) { this.lecturer = lecturer; }
    public Lecture getCoLecturer() { return coLecturer; }
    public void setCoLecturer(Lecture coLecturer) { this.coLecturer = coLecturer; }
    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
    public TopicStatus getStatus() { return status; }
    public void setStatus(TopicStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
