package upt.group5.studymatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.group5.studymatch.model.SelfAssessment;

public interface SelfAssessmentRepository
        extends JpaRepository<SelfAssessment, Long> {

    List<SelfAssessment> findByStudentId(Long studentId);

    boolean existsByStudentIdAndSkillId(
            Long studentId,
            Long skillId
    );
}
