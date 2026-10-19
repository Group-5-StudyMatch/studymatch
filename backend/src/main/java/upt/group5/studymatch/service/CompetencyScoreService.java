package upt.group5.studymatch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.dto.CompetencyScoreDTO;
import upt.group5.studymatch.model.AcademicRecord;
import upt.group5.studymatch.model.CourseUnitSkill;
import upt.group5.studymatch.repository.AcademicRecordRepository;
import upt.group5.studymatch.repository.CourseUnitSkillRepository;
import upt.group5.studymatch.repository.StudentRepository;

@Service
public class CompetencyScoreService {

    private final AcademicRecordRepository academicRecordRepository;
    private final CourseUnitSkillRepository courseUnitSkillRepository;
    private final StudentRepository studentRepository;

    public CompetencyScoreService(
            AcademicRecordRepository academicRecordRepository,
            CourseUnitSkillRepository courseUnitSkillRepository,
            StudentRepository studentRepository) {

        this.academicRecordRepository = academicRecordRepository;
        this.courseUnitSkillRepository = courseUnitSkillRepository;
        this.studentRepository = studentRepository;
    }

    public List<CompetencyScoreDTO> calculateScores(Long studentId) {

        // Check if student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student not found"
            );
        }

        // Get all academic records
        List<AcademicRecord> records =
                academicRecordRepository.findByStudentId(studentId);

        // Group grades by course unit
        Map<Long, List<Integer>> gradesByCourseUnit = new HashMap<>();

        for (AcademicRecord record : records) {

            Long courseUnitId = record.getCourseUnit().getId();

            gradesByCourseUnit
                    .computeIfAbsent(courseUnitId, id -> new ArrayList<>())
                    .add(record.getGrade());
        }

        // Calculate weighted scores for each skill
        Map<Long, Double> weightedSums = new HashMap<>();
        Map<Long, Double> totalWeights = new HashMap<>();
        Map<Long, String> skillNames = new HashMap<>();

        for (Map.Entry<Long, List<Integer>> entry :
                gradesByCourseUnit.entrySet()) {

            Long courseUnitId = entry.getKey();
            List<Integer> grades = entry.getValue();

            double averageGrade = grades.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            List<CourseUnitSkill> mappings =
                    courseUnitSkillRepository.findByCourseUnitId(courseUnitId);

            for (CourseUnitSkill mapping : mappings) {

                Long skillId = mapping.getSkill().getId();
                String skillName = mapping.getSkill().getName();
                double weight = mapping.getWeight();

                weightedSums.merge(
                        skillId,
                        (averageGrade / 20.0) * weight,
                        Double::sum
                );

                totalWeights.merge(skillId, weight, Double::sum);
                skillNames.put(skillId, skillName);
            }
        }

        // Build final competency scores
        List<CompetencyScoreDTO> results = new ArrayList<>();

        for (Long skillId : weightedSums.keySet()) {

            double totalWeight = totalWeights.get(skillId);

            double score = totalWeight > 0.0
                    ? weightedSums.get(skillId) / totalWeight
                    : null;

            results.add(new CompetencyScoreDTO(
                    skillId,
                    skillNames.get(skillId),
                    score
            ));
        }

        return results;
    }
}
