package ute.edu.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review_councils")
public class ReviewCouncil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "chairman_id")
    private Lecture chairman;

    @ManyToOne
    @JoinColumn(name = "secretary_id")
    private Lecture secretary;

    @OneToMany(mappedBy = "council")
    private List<ReviewCouncilMember> members = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ReviewCouncil() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Lecture getChairman() { return chairman; }
    public void setChairman(Lecture chairman) { this.chairman = chairman; }
    public Lecture getSecretary() { return secretary; }
    public void setSecretary(Lecture secretary) { this.secretary = secretary; }
    public List<ReviewCouncilMember> getMembers() { return members; }
    public void setMembers(List<ReviewCouncilMember> members) { this.members = members; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
