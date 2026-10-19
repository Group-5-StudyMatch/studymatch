package upt.group5.studymatch.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.dto.CompetencyScoreDTO;
import upt.group5.studymatch.model.AcademicRecord;
import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.model.CourseUnitSkill;
import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.repository.AcademicRecordRepository;
import upt.group5.studymatch.repository.CourseUnitSkillRepository;
import upt.group5.studymatch.repository.SelfAssessmentRepository;
import upt.group5.studymatch.repository.StudentRepository;

class CompetencyScoreServiceTest {

    @Mock
    private AcademicRecordRepository academicRecordRepository;

    @Mock
    private CourseUnitSkillRepository courseUnitSkillRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SelfAssessmentRepository selfAssessmentRepository;

    private CompetencyScoreService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new CompetencyScoreService(
        academicRecordRepository,
        courseUnitSkillRepository,
        studentRepository,
        selfAssessmentRepository
        );
    }

    @Test
    void shouldRejectStudentThatDoesNotExist() {

        when(studentRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> service.calculateScores(99L)
        );
    }

    @Test
    void shouldReturnEmptyListWhenStudentHasNoGrades() {

        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(academicRecordRepository.findByStudentId(1L))
                .thenReturn(List.of());

        List<CompetencyScoreDTO> result =
                service.calculateScores(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCalculateScoreFromStudentGrade() {

        when(studentRepository.existsById(1L))
                .thenReturn(true);

        CourseUnit courseUnit = mock(CourseUnit.class);
        Skill skill = mock(Skill.class);
        AcademicRecord record = mock(AcademicRecord.class);
        CourseUnitSkill mapping = mock(CourseUnitSkill.class);

        when(courseUnit.getId()).thenReturn(1L);
        when(skill.getId()).thenReturn(1L);
        when(skill.getName()).thenReturn("Programming");

        when(record.getCourseUnit()).thenReturn(courseUnit);
        when(record.getGrade()).thenReturn(16);

        when(mapping.getSkill()).thenReturn(skill);
        when(mapping.getWeight()).thenReturn(0.9);

        when(academicRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(record));

        when(courseUnitSkillRepository.findByCourseUnitId(1L))
                .thenReturn(List.of(mapping));

        List<CompetencyScoreDTO> result =
                service.calculateScores(1L);

        assertEquals(1, result.size());
        assertEquals("Programming", result.get(0).getSkillName());
        assertEquals(0.8, result.get(0).getScore(), 0.0001);
    }

    
    @Test
    void shouldCalculateAverageOfMultipleGrades() {

        when(studentRepository.existsById(1L))
                .thenReturn(true);

        CourseUnit courseUnit = mock(CourseUnit.class);
        Skill skill = mock(Skill.class);

        AcademicRecord record1 = mock(AcademicRecord.class);
        AcademicRecord record2 = mock(AcademicRecord.class);

        CourseUnitSkill mapping = mock(CourseUnitSkill.class);

        when(courseUnit.getId()).thenReturn(1L);

        when(skill.getId()).thenReturn(1L);
        when(skill.getName()).thenReturn("Programming");

        when(record1.getCourseUnit()).thenReturn(courseUnit);
        when(record1.getGrade()).thenReturn(16);

        when(record2.getCourseUnit()).thenReturn(courseUnit);
        when(record2.getGrade()).thenReturn(14);

        when(mapping.getSkill()).thenReturn(skill);
        when(mapping.getWeight()).thenReturn(0.9);

        when(academicRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(record1, record2));

        when(courseUnitSkillRepository.findByCourseUnitId(1L))
                .thenReturn(List.of(mapping));

        List<CompetencyScoreDTO> result =
                service.calculateScores(1L);

        assertEquals(1, result.size());
        assertEquals(0.75, result.get(0).getScore(), 0.0001);
    }

    
    @Test
    void shouldCalculateWeightedAverageFromDifferentCourseUnits() {

        when(studentRepository.existsById(1L))
                .thenReturn(true);

        CourseUnit courseUnit1 = mock(CourseUnit.class);
        CourseUnit courseUnit2 = mock(CourseUnit.class);

        when(courseUnit1.getId()).thenReturn(1L);
        when(courseUnit2.getId()).thenReturn(2L);

        AcademicRecord record1 = mock(AcademicRecord.class);
        AcademicRecord record2 = mock(AcademicRecord.class);

        when(record1.getCourseUnit()).thenReturn(courseUnit1);
        when(record1.getGrade()).thenReturn(16);

        when(record2.getCourseUnit()).thenReturn(courseUnit2);
        when(record2.getGrade()).thenReturn(14);

        Skill skill = mock(Skill.class);

        when(skill.getId()).thenReturn(1L);
        when(skill.getName()).thenReturn("Programming");

        CourseUnitSkill mapping1 = mock(CourseUnitSkill.class);
        CourseUnitSkill mapping2 = mock(CourseUnitSkill.class);

        when(mapping1.getSkill()).thenReturn(skill);
        when(mapping1.getWeight()).thenReturn(0.9);

        when(mapping2.getSkill()).thenReturn(skill);
        when(mapping2.getWeight()).thenReturn(0.7);

        when(academicRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(record1, record2));

        when(courseUnitSkillRepository.findByCourseUnitId(1L))
                .thenReturn(List.of(mapping1));

        when(courseUnitSkillRepository.findByCourseUnitId(2L))
                .thenReturn(List.of(mapping2));

        List<CompetencyScoreDTO> result =
                service.calculateScores(1L);

        assertEquals(1, result.size());
        assertEquals("Programming", result.get(0).getSkillName());
        assertEquals(0.75625, result.get(0).getScore(), 0.0001);
    }


}
