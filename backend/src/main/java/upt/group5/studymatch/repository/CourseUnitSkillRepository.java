package upt.group5.studymatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.group5.studymatch.model.CourseUnitSkill;

public interface CourseUnitSkillRepository
        extends JpaRepository<CourseUnitSkill, Long> {

    List<CourseUnitSkill> findByCourseUnitId(Long courseUnitId);

    boolean existsByCourseUnitIdAndSkillId(
            Long courseUnitId,
            Long skillId
    );
}
