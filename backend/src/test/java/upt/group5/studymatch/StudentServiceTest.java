package upt.group5.studymatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.server.ResponseStatusException;

import upt.group5.studymatch.model.Student;
import upt.group5.studymatch.repository.StudentRepository;
import upt.group5.studymatch.service.StudentService;

public class StudentServiceTest {

    @Test
    void shouldCreateStudentSuccessfully() {

        //criar um repositório simulado
        StudentRepository repository = mock(StudentRepository.class);

        // criar o serviço que queremos testar
        StudentService service = new StudentService(repository);

        // criar um estudante de teste
        Student student = new Student(
            "20260001",
            "Estudante Teste",
            "teste@example.com"
        );

        //simular o comportamento do repositório
        when(repository.save(any(Student.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // executar o método que queremos testar
        Student result = service.createStudent(student);

        // verificar o resultado
        assertEquals("Estudante Teste", result.getName());
        assertEquals("teste@example.com", result.getEmail());

        // confirmar que o estudante foi enviado para guardar
        verify(repository).save(student);
    }

    @Test
    void shouldRejectStudentWithEmptyName() {

        // criar um repositório simulado
        StudentRepository repository = mock(StudentRepository.class);

        // criar o serviço
        StudentService service = new StudentService(repository);

        //criar um estudante com o nome vazio
        Student student = new Student(
            "20260002",
            "",
            "teste2@example.com"
        );

        // verificar se o serviço rejeita o estudante
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.createStudent(student)
        );

        // Confirmar que o erro é 400
        assertEquals(400, exception.getStatusCode().value());

        // confirmar que não tentou guardar o estudante
        verify(repository, never()).save(any(Student.class));
    }

    @Test
    void shouldReturn404WhenStudentDoesNotExist() {

        // criar um repositório simulado
        StudentRepository repository = mock(StudentRepository.class);

        // criar o serviço
        StudentService service = new StudentService(repository);

        // simular que o estudante não existe
        when(repository.findById(9999L))
            .thenReturn(java.util.Optional.empty());

        // tentar procurar o estudante
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.getStudentById(9999L)
        );

        // confirmar que o erro é 404
        assertEquals(404, exception.getStatusCode().value());

        // confirmar que o repositório foi consultado
        verify(repository).findById(9999L);
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() {

        // criar um repositório simulado
        StudentRepository repository = mock(StudentRepository.class);

        // criar o serviço
        StudentService service = new StudentService(repository);

        // criar um estudante de teste
        Student student = new Student(
            "20260003",
            "Outro Estudante",
            "teste@example.com"
        );

        // simular que o email já existe na base de dados
        when(repository.existsByEmail("teste@example.com"))
            .thenReturn(true);

        // tentar criar o estudante
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.createStudent(student)
        );

        // confirmar que o erro é 409
        assertEquals(409, exception.getStatusCode().value());

        // confirmar que o estudante não foi guardado
        verify(repository, never()).save(any(Student.class));
    }

    @Test
    void shouldUpdateStudentSuccessfully() {

        // criar um repositório simulado
        StudentRepository repository = mock(StudentRepository.class);

        // criar o serviço
        StudentService service = new StudentService(repository);

        // criar um estudante que já existe
        Student existingStudent = new Student(
            "20260001",
            "Estudante Antigo",
            "antigo@example.com"
        );
        existingStudent.setId(1L);

        // criar os novos dados
        Student updatedStudent = new Student(
            "20260001",
            "Estudante Atualizado",
            "atualizado@example.com"
        );

        // simular que o estudante existe
        when(repository.findById(1L))
            .thenReturn(java.util.Optional.of(existingStudent));

        // simular que o repositório guarda os dados
        when(repository.save(any(Student.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // atualizar o estudante
        Student result = service.updateStudent(1L, updatedStudent);

        // confirmar os novos dados
        assertEquals(1L, result.getId());
        assertEquals("Estudante Atualizado", result.getName());
        assertEquals("atualizado@example.com", result.getEmail());

        //confirmar que o estudante foi guardado
        verify(repository).save(existingStudent);
    }
}
