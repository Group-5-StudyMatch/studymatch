package upt.group5.studymatch.dto;

import upt.group5.studymatch.model.Role;

public record MeResponse(
        String name,
        Role role,
        Long studentId
) {
}
