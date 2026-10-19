package upt.group5.studymatch.dto;

public class SelfAssessmentSkillDTO {

    private Long skillId;
    private Integer level;

    public SelfAssessmentSkillDTO() {
    }

    public SelfAssessmentSkillDTO(Long skillId, Integer level) {
        this.skillId = skillId;
        this.level = level;
    }

    public Long getSkillId() {
        return skillId;
    }

    public Integer getLevel() {
        return level;
    }
}
