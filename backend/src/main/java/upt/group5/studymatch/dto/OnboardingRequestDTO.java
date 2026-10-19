package upt.group5.studymatch.dto;

import java.util.List;

public class OnboardingRequestDTO {

    private List<SelfAssessmentSkillDTO> skills;

    public OnboardingRequestDTO() {
    }

    public OnboardingRequestDTO(
            List<SelfAssessmentSkillDTO> skills) {

        this.skills = skills;
    }

    public List<SelfAssessmentSkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SelfAssessmentSkillDTO> skills) {
        this.skills = skills;
    }
}
