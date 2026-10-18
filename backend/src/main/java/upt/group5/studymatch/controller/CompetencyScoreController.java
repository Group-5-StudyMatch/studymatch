package upt.group5.studymatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upt.group5.studymatch.dto.CompetencyScoreDTO;
import upt.group5.studymatch.service.CompetencyScoreService;

@RestController
@RequestMapping("/api/students/{studentId}/competencies")
public class CompetencyScoreController {

    private final CompetencyScoreService competencyScoreService;

    public CompetencyScoreController(
            CompetencyScoreService competencyScoreService) {

        this.competencyScoreService = competencyScoreService;
    }

    @GetMapping
    public List<CompetencyScoreDTO> getCompetencyScores(
            @PathVariable Long studentId) {

        return competencyScoreService.calculateScores(studentId);
    }
}
