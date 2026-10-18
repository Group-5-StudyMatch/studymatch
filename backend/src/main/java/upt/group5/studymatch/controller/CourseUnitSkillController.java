package upt.group5.studymatch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upt.group5.studymatch.dto.CourseUnitSkillRequestDTO;
import upt.group5.studymatch.dto.CourseUnitSkillResponseDTO;
import upt.group5.studymatch.model.CourseUnitSkill;
import upt.group5.studymatch.service.CourseUnitSkillService;

import java.util.List;

@RestController
@RequestMapping("/api/course-units/{courseUnitId}/skills")
public class CourseUnitSkillController {

    private final CourseUnitSkillService courseUnitSkillService;

    public CourseUnitSkillController(
            CourseUnitSkillService courseUnitSkillService) {

        this.courseUnitSkillService = courseUnitSkillService;
    }

    @PostMapping
    public ResponseEntity<CourseUnitSkillResponseDTO> addSkill(
            @PathVariable Long courseUnitId,
            @RequestBody CourseUnitSkillRequestDTO request) {

        CourseUnitSkill association =
                courseUnitSkillService.addSkillToCourseUnit(
                        courseUnitId,
                        request.getSkillId(),
                        request.getWeight()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDTO(association));
    }

    @GetMapping
    public List<CourseUnitSkillResponseDTO> getSkills(
            @PathVariable Long courseUnitId) {

        return courseUnitSkillService
                .getSkillsByCourseUnit(courseUnitId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private CourseUnitSkillResponseDTO toDTO(
            CourseUnitSkill association) {

        return new CourseUnitSkillResponseDTO(
                association.getId(),
                association.getCourseUnit().getId(),
                association.getSkill().getId(),
                association.getSkill().getName(),
                association.getWeight()
        );
    }
}
