package upt.group5.studymatch.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.model.Course;
import upt.group5.studymatch.model.CourseUnit;
import upt.group5.studymatch.repository.CourseRepository;
import upt.group5.studymatch.repository.CourseUnitRepository;

@RestController
@RequestMapping("/api/course-units")
public class CourseUnitController {

    private final CourseUnitRepository courseUnitRepository;
    private final CourseRepository courseRepository;

    public CourseUnitController(
            CourseUnitRepository courseUnitRepository,
            CourseRepository courseRepository) {

        this.courseUnitRepository = courseUnitRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public ResponseEntity<CourseUnit> createCourseUnit(
            @RequestBody CourseUnitRequest request) {

        if (request == null || request.getName() == null
                || request.getName().isBlank()
                || request.getCourseId() == null) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Name and course ID are required"
            );
        }

        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Course not found"
            ));

        CourseUnit courseUnit = new CourseUnit(
            request.getName(),
            course
        );

        CourseUnit saved = courseUnitRepository.save(courseUnit);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<CourseUnit> getAllCourseUnits() {
        return courseUnitRepository.findAll();
    }

    public static class CourseUnitRequest {

        private String name;
        private Long courseId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }
    }
}

