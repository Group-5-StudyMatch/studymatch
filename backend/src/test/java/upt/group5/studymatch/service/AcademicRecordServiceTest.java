package upt.group5.studymatch.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.model.AcademicRecord;
import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.repository.AcademicRecordRepository;
import upt.group5.studymatch.repository.CourseUnitRepository;
import upt.group5.studymatch.repository.StudentRepository;

class AcademicRecordServiceTest {

    @Test
    void shouldRejectInvalidGrade() {

        // Criar repositórios simulados
        AcademicRecordRepository academicRecordRepository =
                mock(AcademicRecordRepository.class);

        StudentRepository studentRepository =
                mock(StudentRepository.class);

        CourseUnitRepository courseUnitRepository =
                mock(CourseUnitRepository.class);

        // Criar o Service
        AcademicRecordService service = new AcademicRecordService(
                academicRecordRepository,
                studentRepository,
                courseUnitRepository
        );

        // Tentar adicionar uma nota inválida
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.addAcademicRecord(
                        1L,
                        1L,
                        "Teste 1",
                        25
                )
        );

        // Confirmar que devolve 400 Bad Request
        assertEquals(400, exception.getStatusCode().value());
    }

    
    @Test
    void shouldRejectDuplicateAcademicRecord() {

        // Criar repositórios simulados
        AcademicRecordRepository academicRecordRepository =
                mock(AcademicRecordRepository.class);

        StudentRepository studentRepository =
                mock(StudentRepository.class);

        CourseUnitRepository courseUnitRepository =
                mock(CourseUnitRepository.class);

        // Criar o Service
        AcademicRecordService service = new AcademicRecordService(
                academicRecordRepository,
                studentRepository,
                courseUnitRepository
        );

        // Simular que o estudante existe
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(new Student()));

        // Simular que a unidade curricular existe
        when(courseUnitRepository.findById(1L))
                .thenReturn(Optional.of(new CourseUnit()));

        // Simular que a avaliação já existe
        when(academicRecordRepository
                .existsByStudentIdAndCourseUnitIdAndAssessmentName(
                        1L, 1L, "Teste 1"))
                .thenReturn(true);

        // Tentar adicionar uma avaliação duplicada
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.addAcademicRecord(
                        1L,
                        1L,
                        "Teste 1",
                        16
                )
        );

        // Confirmar que devolve 409 Conflict
        assertEquals(409, exception.getStatusCode().value());
    }
       
    @Test
    void shouldAllowDifferentAssessment() {

        // Criar repositórios simulados
        AcademicRecordRepository academicRecordRepository =
                mock(AcademicRecordRepository.class);

        StudentRepository studentRepository =
                mock(StudentRepository.class);

        CourseUnitRepository courseUnitRepository =
                mock(CourseUnitRepository.class);

        // Simular que o estudante existe
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(new Student()));

        // Simular que a unidade curricular existe
        when(courseUnitRepository.findById(1L))
                .thenReturn(Optional.of(new CourseUnit()));

        // Simular que "Teste 2" ainda não existe
        when(academicRecordRepository
                .existsByStudentIdAndCourseUnitIdAndAssessmentName(
                        1L, 1L, "Teste 2"))
                .thenReturn(false);

        // Criar o Service
        AcademicRecordService service = new AcademicRecordService(
                academicRecordRepository,
                studentRepository,
                courseUnitRepository
        );

        // Adicionar uma avaliação válida
        service.addAcademicRecord(1L, 1L, "Teste 2", 18);

        // Confirmar que o Service tentou guardar a avaliação
        verify(academicRecordRepository)
                .save(any(AcademicRecord.class));
    }

}

