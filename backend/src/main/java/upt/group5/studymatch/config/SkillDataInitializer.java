package upt.group5.studymatch.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.repository.SkillRepository;

@Configuration
public class SkillDataInitializer {

    @Bean
    public CommandLineRunner initializeSkills(
            SkillRepository skillRepository) {

        return args -> {

            String[] predefinedSkills = {
                    "Programming",
                    "Mathematics",
                    "Digital Systems",
                    "Electronics",
                    "Computer Architecture",
                    "Algorithms",
                    "Development Tools",
                    "Networking"
            };

            for (String skillName : predefinedSkills) {

                if (skillRepository.findByName(skillName).isEmpty()) {
                    skillRepository.save(new Skill(skillName));
                }
            }
        };
    }
}
