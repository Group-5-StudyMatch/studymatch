package upt.group5.studymatch.dto;

public class CourseUnitSkillRequestDTO {

    private Long skillId;
    private Double weight;

    public CourseUnitSkillRequestDTO() {
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}

