package upt.group5.studymatch.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upt.group5.studymatch.dto.AcademicRecordRequestDTO;
import upt.group5.studymatch.dto.AcademicRecordResponseDTO;
import upt.group5.studymatch.model.AcademicRecord;
import upt.group5.studymatch.service.AcademicRecordService;

@RestController
@RequestMapping("/api/students/{studentId}/academic-records")
public class AcademicRecordController {

    private final AcademicRecordService academicRecordService;

    public AcademicRecordController(
            AcademicRecordService academicRecordService) {
        this.academicRecordService = academicRecordService;
    }

    @PostMapping
    public ResponseEntity<AcademicRecordResponseDTO> addAcademicRecord(
            @PathVariable Long studentId,
            @RequestBody AcademicRecordRequestDTO request) {

        AcademicRecord record = academicRecordService.addAcademicRecord(
            studentId,
            request.getCourseUnitId(),
            request.getAssessmentName(),
            request.getGrade()
        );

        AcademicRecordResponseDTO response = new AcademicRecordResponseDTO(
            record.getId(),
            record.getStudent().getId(),
            record.getCourseUnit().getId(),
            record.getAssessmentName(),
            record.getGrade()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @GetMapping
    public List<AcademicRecordResponseDTO> getAcademicRecords(
            @PathVariable Long studentId) {

        return academicRecordService
            .getAcademicRecordsByStudent(studentId)
            .stream()
            .map(record -> new AcademicRecordResponseDTO(
                record.getId(),
                record.getStudent().getId(),
                record.getCourseUnit().getId(),
                record.getAssessmentName(),
                record.getGrade()
            ))
            .toList();
    }

}
