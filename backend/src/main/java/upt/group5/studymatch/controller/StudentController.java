package upt.group5.studymatch.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upt.group5.studymatch.dto.StudentRequestDTO;
import upt.group5.studymatch.dto.StudentResponseDTO;
import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Criar um estudante
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody StudentRequestDTO request) {

        // converter os dados recebidos num Student
        Student student = new Student(
            request.getInstitutionalNumber(),
            request.getName(),
            request.getEmail()
        );

        // guardar o estudante através do serviço
        Student createdStudent = studentService.createStudent(student);

        // Converter o estudante guardado num DTO de resposta
        StudentResponseDTO response = new StudentResponseDTO(
            createdStudent.getId(),
            createdStudent.getInstitutionalNumber(),
            createdStudent.getName(),
            createdStudent.getEmail()
        );

        // Devolver HTTP 201 com os dados do estudante
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar todos os estudantes
    @GetMapping
    public List<StudentResponseDTO> getAllStudents() {
        return studentService.getAllStudents()
            .stream()
            .map(student -> new StudentResponseDTO(
                student.getId(),
                student.getInstitutionalNumber(),
                student.getName(),
                student.getEmail()
            ))
            .collect(Collectors.toList());
    }

    // Consultar um estudante pelo ID
    @GetMapping("/{id}")
    public StudentResponseDTO getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);

        return new StudentResponseDTO(
            student.getId(),
            student.getInstitutionalNumber(),
            student.getName(),
            student.getEmail()
        );
    }

    // Atualizar um estudante
    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(@PathVariable Long id,@RequestBody StudentRequestDTO request) {

        // converter o DTO recebido num Student
        Student student = new Student(
            request.getInstitutionalNumber(),
            request.getName(),
            request.getEmail()
        );

        // atualizar o estudante através do serviço
        Student updatedStudent = studentService.updateStudent(id, student);

        // converter o estudante atualizado num DTO de resposta
        return new StudentResponseDTO(
            updatedStudent.getId(),
            updatedStudent.getInstitutionalNumber(),
            updatedStudent.getName(),
            updatedStudent.getEmail()
        );
    }
}

