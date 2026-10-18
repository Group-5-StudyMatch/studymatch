package upt.group5.studymatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.group5.studymatch.model.AcademicRecord;

public interface AcademicRecordRepository
        extends JpaRepository<AcademicRecord, Long> {

    boolean existsByStudentIdAndCourseUnitIdAndAssessmentName(
            Long studentId,
            Long courseUnitId,
            String assessmentName
    );

    List<AcademicRecord> findByStudentId(Long studentId);
}


