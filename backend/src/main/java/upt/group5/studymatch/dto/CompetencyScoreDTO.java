package upt.group5.studymatch.dto;

public class CompetencyScoreDTO {

    private Long skillId;
    private String skillName;
    private Double score;

    public CompetencyScoreDTO() {
    }

    public CompetencyScoreDTO(
            Long skillId,
            String skillName,
            Double score) {

        this.skillId = skillId;
        this.skillName = skillName;
        this.score = score;
    }

    public Long getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public Double getScore() {
        return score;
    }
}
