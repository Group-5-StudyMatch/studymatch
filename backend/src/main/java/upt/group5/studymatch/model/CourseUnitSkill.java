package upt.group5.studymatch.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "course_unit_skills",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"course_unit_id", "skill_id"}
    )
)
public class CourseUnitSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_unit_id", nullable = false)
    private CourseUnit courseUnit;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Double weight;

    public CourseUnitSkill() {
    }

    public CourseUnitSkill(CourseUnit courseUnit, Skill skill, Double weight) {
        this.courseUnit = courseUnit;
        this.skill = skill;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public CourseUnit getCourseUnit() {
        return courseUnit;
    }

    public void setCourseUnit(CourseUnit courseUnit) {
        this.courseUnit = courseUnit;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
