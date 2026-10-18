package upt.group5.studymatch.dto;

public class AcademicRecordRequestDTO {

    private Long courseUnitId;
    private String assessmentName;
    private Integer grade;

    public AcademicRecordRequestDTO() {
    }

    public Long getCourseUnitId() {
        return courseUnitId;
    }

    public void setCourseUnitId(Long courseUnitId) {
        this.courseUnitId = courseUnitId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
