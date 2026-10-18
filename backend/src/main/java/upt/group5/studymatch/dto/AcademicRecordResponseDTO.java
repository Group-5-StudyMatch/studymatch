package upt.group5.studymatch.dto;

public class AcademicRecordResponseDTO {

    private Long id;
    private Long studentId;
    private Long courseUnitId;
    private String assessmentName;
    private Integer grade;

    public AcademicRecordResponseDTO() {
    }

    public AcademicRecordResponseDTO(
            Long id,
            Long studentId,
            Long courseUnitId,
            String assessmentName,
            Integer grade) {

        this.id = id;
        this.studentId = studentId;
        this.courseUnitId = courseUnitId;
        this.assessmentName = assessmentName;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getCourseUnitId() {
        return courseUnitId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public Integer getGrade() {
        return grade;
    }
}
