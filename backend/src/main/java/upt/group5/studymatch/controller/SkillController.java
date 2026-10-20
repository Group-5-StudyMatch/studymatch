package upt.group5.studymatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.repository.SkillRepository;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    public record SkillResponse(Long id, String name) {
    }

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll()
        .stream()
        .map(skill -> new SkillResponse(skill.getId(), skill.getName()))
        .toList();  
    }
}
