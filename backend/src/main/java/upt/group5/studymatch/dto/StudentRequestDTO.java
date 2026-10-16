package upt.group5.studymatch.dto;

public class StudentRequestDTO {

    private String institutionalNumber;
    private String name;
    private String email;

    public StudentRequestDTO() {
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
