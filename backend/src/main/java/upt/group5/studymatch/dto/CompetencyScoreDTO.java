package upt.group5.studymatch.dto;

import upt.group5.studymatch.model.CompetencyScoreSource;

public class CompetencyScoreDTO {

    private Long skillId;
    private String skillName;
    private Double score;
    private CompetencyScoreSource source;

    public CompetencyScoreDTO() {
    }

    public CompetencyScoreDTO(
            Long skillId,
            String skillName,
            Double score,
            CompetencyScoreSource source) {

        this.skillId = skillId;
        this.skillName = skillName;
        this.score = score;
        this.source = source;
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

    public CompetencyScoreSource getSource() {
        return source;
    }
}
