package upt.group5.studymatch.dto;

public class CourseUnitSkillResponseDTO {

    private Long id;
    private Long courseUnitId;
    private Long skillId;
    private String skillName;
    private Double weight;

    public CourseUnitSkillResponseDTO() {
    }

    public CourseUnitSkillResponseDTO(
            Long id,
            Long courseUnitId,
            Long skillId,
            String skillName,
            Double weight) {

        this.id = id;
        this.courseUnitId = courseUnitId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public Long getCourseUnitId() {
        return courseUnitId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public Double getWeight() {
        return weight;
    }
}
