package upt.group5.studymatch.dto;

import upt.group5.studymatch.model.Role;

public record LoginResponse(
        String token, //JWT utilizado para autenticar os pedidos seguintes
        Role role,
        String name,
        Long studentId
) {
}
