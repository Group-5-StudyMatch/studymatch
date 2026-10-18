package upt.group5.studymatch.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.model.CourseUnitSkill;
import upt.group5.studymatch.model.Skill;

import upt.group5.studymatch.repository.CourseUnitRepository;
import upt.group5.studymatch.repository.CourseUnitSkillRepository;
import upt.group5.studymatch.repository.SkillRepository;

import java.util.List;

@Service
public class CourseUnitSkillService {

    private final CourseUnitSkillRepository courseUnitSkillRepository;
    private final CourseUnitRepository courseUnitRepository;
    private final SkillRepository skillRepository;

    public CourseUnitSkillService(
            CourseUnitSkillRepository courseUnitSkillRepository,
            CourseUnitRepository courseUnitRepository,
            SkillRepository skillRepository) {

        this.courseUnitSkillRepository = courseUnitSkillRepository;
        this.courseUnitRepository = courseUnitRepository;
        this.skillRepository = skillRepository;
    }

    public CourseUnitSkill addSkillToCourseUnit(
            Long courseUnitId,
            Long skillId,
            Double weight) {

        // 1. Validate weight
        if (weight == null || !Double.isFinite(weight)
                || weight < 0.0 || weight > 1.0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Weight must be between 0.0 and 1.0"
            );
        }

        // 2. Find course unit
        CourseUnit courseUnit = courseUnitRepository
                .findById(courseUnitId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course unit not found"
                ));

        // 3. Find skill
        Skill skill = skillRepository
                .findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Skill not found"
                ));

        // 4. Check for duplicate association
        if (courseUnitSkillRepository
                .existsByCourseUnitIdAndSkillId(courseUnitId, skillId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Skill already associated with this course unit"
            );
        }

        // 5. Save association
        CourseUnitSkill association =
                new CourseUnitSkill(courseUnit, skill, weight);

        return courseUnitSkillRepository.save(association);
    }

    public List<CourseUnitSkill> getSkillsByCourseUnit(Long courseUnitId) {

        if (!courseUnitRepository.existsById(courseUnitId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course unit not found"
            );
        }

        return courseUnitSkillRepository.findByCourseUnitId(courseUnitId);
    }
}

