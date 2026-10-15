package upt.group5.studymatch.dto;

public class StudentResponseDTO {

    private Long id;
    private String institutionalNumber;
    private String name;
    private String email;

    public StudentResponseDTO() {
    }

    public StudentResponseDTO(Long id, String institutionalNumber,
                              String name, String email) {
        this.id = id;
        this.institutionalNumber = institutionalNumber;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getInstitutionalNumber() {
        return institutionalNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
