package upt.group5.studymatch.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import upt.group5.studymatch.model.AcademicRecord;
import upt.group5.studymatch.model.Course;
import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.model.CourseUnitSkill;
import upt.group5.studymatch.model.SelfAssessment;
import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.repository.AcademicRecordRepository;
import upt.group5.studymatch.repository.CourseRepository;
import upt.group5.studymatch.repository.CourseUnitRepository;
import upt.group5.studymatch.repository.CourseUnitSkillRepository;
import upt.group5.studymatch.repository.SelfAssessmentRepository;
import upt.group5.studymatch.repository.SkillRepository;
import upt.group5.studymatch.repository.StudentRepository;

@Component
@Profile("!test")
public class DemoDataSeeder implements CommandLineRunner {

    private record Mapping(String skill, double weight) {
    }

    // Skill order used by the cold-start self-assessment levels below.
    private static final List<String> SKILL_ORDER = List.of(
            "Programming", "Mathematics", "Digital Systems", "Electronics",
            "Computer Architecture", "Algorithms", "Development Tools", "Networking");

    // One row per cold-start student: self-assessment level (0-3) for each
    // skill in SKILL_ORDER. Every row is different.
    private static final int[][] COLD_START_LEVELS = {
            {3, 2, 1, 0, 1, 2, 3, 1},
            {0, 1, 2, 3, 2, 1, 0, 2},
            {2, 3, 3, 1, 0, 2, 1, 0},
            {1, 0, 2, 2, 3, 3, 0, 1},
            {3, 3, 0, 1, 2, 0, 2, 3},
            {1, 2, 1, 3, 0, 1, 2, 2},
            {2, 0, 3, 2, 1, 3, 1, 0}
    };

    private final CourseRepository courseRepository;
    private final CourseUnitRepository courseUnitRepository;
    private final CourseUnitSkillRepository courseUnitSkillRepository;
    private final SkillRepository skillRepository;
    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final SelfAssessmentRepository selfAssessmentRepository;

    public DemoDataSeeder(
            CourseRepository courseRepository,
            CourseUnitRepository courseUnitRepository,
            CourseUnitSkillRepository courseUnitSkillRepository,
            SkillRepository skillRepository,
            StudentRepository studentRepository,
            AcademicRecordRepository academicRecordRepository,
            SelfAssessmentRepository selfAssessmentRepository) {

        this.courseRepository = courseRepository;
        this.courseUnitRepository = courseUnitRepository;
        this.courseUnitSkillRepository = courseUnitSkillRepository;
        this.skillRepository = skillRepository;
        this.studentRepository = studentRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.selfAssessmentRepository = selfAssessmentRepository;
    }

    @Override
    public void run(String... args) {

        // only seed when the students table is empty (idempotent)
        if (studentRepository.count() > 0) {
            return;
        }

        // skills by name. Missing ones are created, so the run order
        // relative to SkillDataInitializer does not matter.
        Map<String, Skill> skills = new HashMap<>(
                skillRepository.findAll().stream()
                        .collect(Collectors.toMap(Skill::getName, Function.identity())));

        // course unit -> skills it contributes to, with weights
        Map<String, List<Mapping>> units = new LinkedHashMap<>();
        units.put("Programming 1", List.of(
                new Mapping("Programming", 0.9),
                new Mapping("Algorithms", 0.6),
                new Mapping("Development Tools", 0.3)));
        units.put("Algorithms and Data Structures", List.of(
                new Mapping("Algorithms", 0.9),
                new Mapping("Programming", 0.6),
                new Mapping("Mathematics", 0.3)));
        units.put("Mathematics 1", List.of(
                new Mapping("Mathematics", 0.9),
                new Mapping("Algorithms", 0.4)));
        units.put("Digital Systems", List.of(
                new Mapping("Digital Systems", 0.9),
                new Mapping("Computer Architecture", 0.5),
                new Mapping("Electronics", 0.4)));
        units.put("Computer Architecture", List.of(
                new Mapping("Computer Architecture", 0.9),
                new Mapping("Digital Systems", 0.4)));
        units.put("Computer Networks", List.of(
                new Mapping("Networking", 0.9),
                new Mapping("Development Tools", 0.3)));

        // reuse existing course and course units so nothing is duplicated
        Course course = courseRepository.findAll().stream().findFirst()
                .orElseGet(() -> courseRepository.save(new Course("Computer Engineering")));

        Map<String, CourseUnit> existingUnits = courseUnitRepository.findAll().stream()
                .collect(Collectors.toMap(CourseUnit::getName, Function.identity(), (a, b) -> a));

        Map<String, CourseUnit> savedUnits = new LinkedHashMap<>();
        units.forEach((unitName, mappings) -> {
            CourseUnit unit = existingUnits.get(unitName);
            if (unit == null) {
                unit = courseUnitRepository.save(new CourseUnit(unitName, course));
            }
            final CourseUnit savedUnit = unit;
            savedUnits.put(unitName, unit);

            for (Mapping m : mappings) {
                Skill skill = skills.computeIfAbsent(
                        m.skill(), name -> skillRepository.save(new Skill(name)));
                if (!courseUnitSkillRepository.existsByCourseUnitIdAndSkillId(
                        savedUnit.getId(), skill.getId())) {
                    courseUnitSkillRepository.save(
                            new CourseUnitSkill(savedUnit, skill, m.weight()));
                }
            }
        });

        List<CourseUnit> unitList = new ArrayList<>(savedUnits.values());

        // 1. 16 students (8 betweeen 20 notes)
        for (int i = 1; i <= 16; i++) {
            String instNum = String.format("202610%02d", i);
            String name = "Academic Student " + i;
            String email = "academic.student" + i + "@studymatch.upt.pt";

            Student student = studentRepository.save(new Student(instNum, name, email));

            for (int j = 0; j < 3; j++) {
                CourseUnit unit = unitList.get((i + j) % unitList.size());
                int grade = 8 + ((i * 3 + j * 5) % 13); // 8-20 arası not üretimi
                
                academicRecordRepository.save(new AcademicRecord(
                        student, unit, "Final Exam", grade));
            }
        }

        // 2. 7 Cold-Start Student (without notes)
        for (int i = 1; i <= 7; i++) {
            String instNum = String.format("202620%02d", i);
            String name = "ColdStart Student " + i;
            String email = "coldstart.student" + i + "@studymatch.upt.pt";

            Student student = studentRepository.save(new Student(instNum, name, email));

            // completed onboarding: one answer (level 0-3) for each of the 8 skills
            int[] levels = COLD_START_LEVELS[i - 1];
            for (int k = 0; k < SKILL_ORDER.size(); k++) {
                Skill skill = skills.get(SKILL_ORDER.get(k));
                selfAssessmentRepository.save(
                        new SelfAssessment(student, skill, levels[k]));
            }
        }
    }
}