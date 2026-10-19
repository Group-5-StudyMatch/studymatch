package upt.group5.studymatch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "self_assessments",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"student_id", "skill_id"}
    )
)
public class SelfAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Integer level;

    public SelfAssessment() {
    }

    public SelfAssessment(
            Student student,
            Skill skill,
            Integer level) {

        this.student = student;
        this.skill = skill;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Skill getSkill() {
        return skill;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
