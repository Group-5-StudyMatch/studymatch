
package upt.group5.studymatch.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    //criar estudante
    public Student createStudent(Student student) {
        validateStudent(student);

        //verificar email
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email already exists"
            );
        }

        student.setId(null);
        return studentRepository.save(student);
    }

    // Listar todos os estudantes
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Procurar estudante pelo ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Student not found"
            ));
    }

    // Atualizar estudante
    public Student updateStudent(Long id, Student updatedStudent) {
        Student student = getStudentById(id);

        validateStudent(updatedStudent);

        if (!student.getEmail().equals(updatedStudent.getEmail())
                && studentRepository.existsByEmail(updatedStudent.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email already exists"
            );
        }

        student.setInstitutionalNumber(updatedStudent.getInstitutionalNumber());
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());

        return studentRepository.save(student);
    }

    // Validar dados recebidos
    private void validateStudent(Student student) {
        if (student == null
                || isBlank(student.getInstitutionalNumber())
                || isBlank(student.getName())
                || isBlank(student.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Institutional number, name and email are required"
            );
        }

        if (!student.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid email format"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
