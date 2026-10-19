package upt.group5.studymatch.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.dto.OnboardingRequestDTO;
import upt.group5.studymatch.dto.SelfAssessmentSkillDTO;
import upt.group5.studymatch.model.SelfAssessment;
import upt.group5.studymatch.model.Skill;
import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.repository.AcademicRecordRepository;
import upt.group5.studymatch.repository.SelfAssessmentRepository;
import upt.group5.studymatch.repository.SkillRepository;
import upt.group5.studymatch.repository.StudentRepository;

@Service
public class OnboardingService {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final SelfAssessmentRepository selfAssessmentRepository;

    public OnboardingService(
            StudentRepository studentRepository,
            SkillRepository skillRepository,
            AcademicRecordRepository academicRecordRepository,
            SelfAssessmentRepository selfAssessmentRepository) {

        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.selfAssessmentRepository = selfAssessmentRepository;
    }

    @Transactional
    public void submitOnboarding(
            Long studentId,
            OnboardingRequestDTO request) {

        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found"
                ));

        // Cold-start is only for students without academic history
        if (!academicRecordRepository.findByStudentId(studentId).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student already has academic history"
            );
        }

        // Validate questionnaire
        if (request == null || request.getSkills() == null
                || request.getSkills().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Skills are required"
            );
        }

        
        List<SelfAssessmentSkillDTO> answers = request.getSkills();

        // The questionnaire requires all 8 technical skills
        if (answers.size() != 8) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The questionnaire must contain exactly 8 skills"
        );
        }

        Set<Long> submittedSkillIds = new HashSet<>();

        for (SelfAssessmentSkillDTO answer : answers) {

            if (answer == null
                    || answer.getSkillId() == null
                    || answer.getLevel() == null
                    || answer.getLevel() < 0
                    || answer.getLevel() > 3) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid self-assessment answer"
                );
            }

            if (!submittedSkillIds.add(answer.getSkillId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate skill in questionnaire"
                );
            }

            if (!skillRepository.existsById(answer.getSkillId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid skill"
                );
            }

            if (selfAssessmentRepository.existsByStudentIdAndSkillId(
                    studentId, answer.getSkillId())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Self-assessment already submitted for this skill"
                );
            }
        }

        
        
        Set<Long> requiredSkillIds = new HashSet<>();

        skillRepository.findAll().forEach(skill ->
                requiredSkillIds.add(skill.getId())
        );

        if (!submittedSkillIds.equals(requiredSkillIds)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The questionnaire must include all predefined skills"
        );
}


        // Save answers after validation
        for (SelfAssessmentSkillDTO answer : answers) {

            Skill skill = skillRepository.findById(answer.getSkillId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid skill"
                    ));

            SelfAssessment assessment = new SelfAssessment(
                    student,
                    skill,
                    answer.getLevel()
            );

            selfAssessmentRepository.save(assessment);
        }
    }
}
