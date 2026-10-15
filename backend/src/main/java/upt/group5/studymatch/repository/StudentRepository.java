package upt.group5.studymatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.group5.studymatch.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);
}
