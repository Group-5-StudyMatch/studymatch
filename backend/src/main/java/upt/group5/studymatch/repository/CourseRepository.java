package upt.group5.studymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.group5.studymatch.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}

