package upt.group5.studymatch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //entidade guardada na bd
@Table(name = "students")
public class Student {

    @Id //chave primaria da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) //gera automaticamente o id
    private Long id;

    @Column(nullable = false)
    private String institutionalNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    //permite ao Hibernate/JPA criar objetos Student automaticamente quando lê os dados da bd
    public Student() {

    }

    //para a criação
    public Student(String institutionalNumber, String name, String email) {
        this.institutionalNumber = institutionalNumber;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
    this.id = id;
    }

    public String getInstitutionalNumber() {
        return institutionalNumber;
    }

    public void setInstitutionalNumber(String institutionalNumber) {
        this.institutionalNumber = institutionalNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
