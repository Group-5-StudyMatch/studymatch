package upt.group5.studymatch.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import upt.group5.studymatch.model.Course;
import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.model.CourseUnitSkill;
import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.repository.CourseRepository;
import upt.group5.studymatch.repository.CourseUnitRepository;
import upt.group5.studymatch.repository.CourseUnitSkillRepository;
import upt.group5.studymatch.repository.SkillRepository;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private record Mapping(String skill, double weight) {
    }

    private final CourseRepository courseRepository;
    private final CourseUnitRepository courseUnitRepository;
    private final CourseUnitSkillRepository courseUnitSkillRepository;
    private final SkillRepository skillRepository;

    public DemoDataSeeder(
            CourseRepository courseRepository,
            CourseUnitRepository courseUnitRepository,
            CourseUnitSkillRepository courseUnitSkillRepository,
            SkillRepository skillRepository) {

        this.courseRepository = courseRepository;
        this.courseUnitRepository = courseUnitRepository;
        this.courseUnitSkillRepository = courseUnitSkillRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(String... args) {

        // only seed once (if mappings already exist, do nothing)
        if (courseUnitSkillRepository.count() > 0) {
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

        Course course = courseRepository.save(new Course("Computer Engineering"));

        units.forEach((unitName, mappings) -> {
            CourseUnit unit = courseUnitRepository.save(new CourseUnit(unitName, course));

            for (Mapping m : mappings) {
                Skill skill = skills.computeIfAbsent(
                        m.skill(), name -> skillRepository.save(new Skill(name)));
                courseUnitSkillRepository.save(
                        new CourseUnitSkill(unit, skill, m.weight()));
            }
        });
    }
}