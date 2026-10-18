package upt.group5.studymatch.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.model.AcademicRecord;
import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.repository.AcademicRecordRepository;
import upt.group5.studymatch.repository.CourseUnitRepository;
import upt.group5.studymatch.repository.StudentRepository;

@Service
public class AcademicRecordService {

    private final AcademicRecordRepository academicRecordRepository;
    private final StudentRepository studentRepository;
    private final CourseUnitRepository courseUnitRepository;

    public AcademicRecordService(
            AcademicRecordRepository academicRecordRepository,
            StudentRepository studentRepository,
            CourseUnitRepository courseUnitRepository) {

        this.academicRecordRepository = academicRecordRepository;
        this.studentRepository = studentRepository;
        this.courseUnitRepository = courseUnitRepository;
    }

    public AcademicRecord addAcademicRecord(
            Long studentId,
            Long courseUnitId,
            String assessmentName,
            Integer grade) {

        // 1. Validar a nota
        if (grade == null || grade < 0 || grade > 20) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Grade must be between 0 and 20"
            );
        }

        // 2. Validar o nome da avaliação
        if (assessmentName == null || assessmentName.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Assessment name is required"
            );
        }

        // 3. Verificar se o estudante existe
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Student not found"
            ));

        // 4. Verificar se a unidade curricular existe
        CourseUnit courseUnit = courseUnitRepository.findById(courseUnitId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Course unit not found"
            ));

        // 5. Verificar se a avaliação já existe
        boolean duplicate = academicRecordRepository
            .existsByStudentIdAndCourseUnitIdAndAssessmentName(
                studentId,
                courseUnitId,
                assessmentName
            );

        if (duplicate) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Academic record already exists"
            );
        }

        // 6. Criar o registo académico
        AcademicRecord record = new AcademicRecord(
            student,
            courseUnit,
            assessmentName,
            grade
        );

        // 7. Guardar na base de dados
        return academicRecordRepository.save(record);

        
    }

    
public List<AcademicRecord> getAcademicRecordsByStudent(Long studentId) {

    // Verificar se o estudante existe
    if (!studentRepository.existsById(studentId)) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Student not found"
        );
    }

    // Procurar todas as avaliações do estudante
    return academicRecordRepository.findByStudentId(studentId);
    }

}

