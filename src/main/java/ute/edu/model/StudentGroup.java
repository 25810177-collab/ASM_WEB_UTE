package ute.edu.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_groups")
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "leader_id", nullable = false)
    private Student leader;

    @OneToMany(mappedBy = "group")
    private List<StudentGroupMember> members = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StudentGroup() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Student getLeader() { return leader; }
    public void setLeader(Student leader) { this.leader = leader; }
    public List<StudentGroupMember> getMembers() { return members; }
    public void setMembers(List<StudentGroupMember> members) { this.members = members; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
