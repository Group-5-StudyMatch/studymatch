package upt.group5.studymatch.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.model.CourseUnitSkill;
import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.repository.CourseUnitRepository;
import upt.group5.studymatch.repository.CourseUnitSkillRepository;
import upt.group5.studymatch.repository.SkillRepository;

public class CourseUnitSkillServiceTest {

    @Mock
    private CourseUnitSkillRepository courseUnitSkillRepository;

    @Mock
    private CourseUnitRepository courseUnitRepository;

    @Mock
    private SkillRepository skillRepository;

    private CourseUnitSkillService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new CourseUnitSkillService(
                courseUnitSkillRepository,
                courseUnitRepository,
                skillRepository
        );
    }

    @Test
    void shouldRejectInvalidWeight() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.addSkillToCourseUnit(
                                1L, 1L, 1.5
                        )
                );

        assertEquals(400, exception.getStatusCode().value());

        verifyNoInteractions(
                courseUnitRepository,
                skillRepository,
                courseUnitSkillRepository
        );
    }

    @Test
    void shouldRejectDuplicateAssociation() {

        when(courseUnitRepository.findById(1L))
                .thenReturn(Optional.of(mock(CourseUnit.class)));

        when(skillRepository.findById(1L))
                .thenReturn(Optional.of(mock(Skill.class)));

        when(courseUnitSkillRepository
                .existsByCourseUnitIdAndSkillId(1L, 1L))
                .thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.addSkillToCourseUnit(
                                1L, 1L, 0.9
                        )
                );

        assertEquals(409, exception.getStatusCode().value());

        verify(courseUnitSkillRepository, never())
                .save(any(CourseUnitSkill.class));
    }

    @Test
    void shouldAllowValidAssociation() {

        CourseUnit courseUnit = mock(CourseUnit.class);
        Skill skill = mock(Skill.class);

        when(courseUnitRepository.findById(1L))
                .thenReturn(Optional.of(courseUnit));

        when(skillRepository.findById(1L))
                .thenReturn(Optional.of(skill));

        when(courseUnitSkillRepository
                .existsByCourseUnitIdAndSkillId(1L, 1L))
                .thenReturn(false);

        when(courseUnitSkillRepository
                .save(any(CourseUnitSkill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CourseUnitSkill result =
                service.addSkillToCourseUnit(1L, 1L, 0.9);

        assertNotNull(result);
        assertEquals(0.9, result.getWeight());
        assertSame(courseUnit, result.getCourseUnit());
        assertSame(skill, result.getSkill());

        verify(courseUnitSkillRepository)
                .save(any(CourseUnitSkill.class));
    }
}

