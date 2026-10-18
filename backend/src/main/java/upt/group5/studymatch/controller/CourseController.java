package upt.group5.studymatch.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upt.group5.studymatch.model.Course;
import upt.group5.studymatch.repository.CourseRepository;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {

        if (course == null || course.getName() == null
                || course.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        course.setId(null);

        Course savedCourse = courseRepository.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}

