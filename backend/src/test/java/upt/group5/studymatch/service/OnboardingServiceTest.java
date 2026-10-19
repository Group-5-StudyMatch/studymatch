package upt.group5.studymatch.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.dto.OnboardingRequestDTO;
import upt.group5.studymatch.dto.SelfAssessmentSkillDTO;
import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.repository.AcademicRecordRepository;
import upt.group5.studymatch.repository.SelfAssessmentRepository;
import upt.group5.studymatch.repository.SkillRepository;
import upt.group5.studymatch.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private AcademicRecordRepository academicRecordRepository;

    @Mock
    private SelfAssessmentRepository selfAssessmentRepository;

    private OnboardingService service;

    @BeforeEach
    void setUp() {
        service = new OnboardingService(
                studentRepository,
                skillRepository,
                academicRecordRepository,
                selfAssessmentRepository
        );
    }

    
        
    
    
        @Test
        void shouldSaveValidSelfAssessment() {

        Student student = new Student();

        when(studentRepository.findById(3L))
                .thenReturn(Optional.of(student));

        when(academicRecordRepository.findByStudentId(3L))
                .thenReturn(List.of());

        List<SelfAssessmentSkillDTO> answers = List.of(
                new SelfAssessmentSkillDTO(1L, 2),
                new SelfAssessmentSkillDTO(2L, 3),
                new SelfAssessmentSkillDTO(3L, 1),
                new SelfAssessmentSkillDTO(4L, 2),
                new SelfAssessmentSkillDTO(5L, 1),
                new SelfAssessmentSkillDTO(6L, 3),
                new SelfAssessmentSkillDTO(7L, 2),
                new SelfAssessmentSkillDTO(8L, 1)
        );

        for (long id = 1; id <= 8; id++) {
                when(skillRepository.existsById(id))
                        .thenReturn(true);

                when(skillRepository.findById(id))
                        .thenReturn(Optional.of(new Skill("Skill " + id)));
        }

        
        List<Skill> predefinedSkills = new java.util.ArrayList<>();

        for (long id = 1; id <= 8; id++) {
        Skill skill = mock(Skill.class);

        when(skill.getId()).thenReturn(id);

        predefinedSkills.add(skill);
        }

        when(skillRepository.findAll())
                .thenReturn(predefinedSkills);

        
        OnboardingRequestDTO request =
                new OnboardingRequestDTO(answers);

        service.submitOnboarding(3L, request);

        verify(selfAssessmentRepository, times(8))
                .save(any());
        }


    @Test
    void shouldRejectStudentThatDoesNotExist() {

        when(studentRepository.findById(99L))
                .thenReturn(Optional.empty());

        OnboardingRequestDTO request = new OnboardingRequestDTO(
                List.of(new SelfAssessmentSkillDTO(1L, 2))
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.submitOnboarding(99L, request)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void shouldRejectInvalidSkillLevel() {

        when(studentRepository.findById(3L))
                .thenReturn(Optional.of(new Student()));

        when(academicRecordRepository.findByStudentId(3L))
                .thenReturn(List.of());

        OnboardingRequestDTO request = new OnboardingRequestDTO(
                List.of(new SelfAssessmentSkillDTO(1L, 5))
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.submitOnboarding(3L, request)
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(selfAssessmentRepository, never())
                .save(any());
    }

    
        @Test
        void shouldRejectDuplicateSkills() {

        when(studentRepository.findById(3L))
                .thenReturn(Optional.of(new Student()));

        when(academicRecordRepository.findByStudentId(3L))
                .thenReturn(List.of());

        // 8 answers, but skill 1 appears twice
        OnboardingRequestDTO request = new OnboardingRequestDTO(
                List.of(
                        new SelfAssessmentSkillDTO(1L, 2),
                        new SelfAssessmentSkillDTO(2L, 3),
                        new SelfAssessmentSkillDTO(3L, 1),
                        new SelfAssessmentSkillDTO(4L, 2),
                        new SelfAssessmentSkillDTO(5L, 1),
                        new SelfAssessmentSkillDTO(6L, 3),
                        new SelfAssessmentSkillDTO(7L, 2),
                        new SelfAssessmentSkillDTO(1L, 3)
                )
        );

        when(skillRepository.existsById(1L))
                .thenReturn(true);

        when(skillRepository.existsById(2L))
                .thenReturn(true);

        when(skillRepository.existsById(3L))
                .thenReturn(true);

        when(skillRepository.existsById(4L))
                .thenReturn(true);

        when(skillRepository.existsById(5L))
                .thenReturn(true);

        when(skillRepository.existsById(6L))
                .thenReturn(true);

        when(skillRepository.existsById(7L))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.submitOnboarding(3L, request)
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(selfAssessmentRepository, never())
                .save(any());
        }


    @Test
    void shouldRejectEmptyQuestionnaire() {

        when(studentRepository.findById(3L))
                .thenReturn(Optional.of(new Student()));

        when(academicRecordRepository.findByStudentId(3L))
                .thenReturn(List.of());

        OnboardingRequestDTO request = new OnboardingRequestDTO(
                List.of()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.submitOnboarding(3L, request)
        );

        assertEquals(400, exception.getStatusCode().value());
    }
}
