package upt.group5.studymatch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upt.group5.studymatch.dto.LoginRequest;
import upt.group5.studymatch.dto.LoginResponse;
import upt.group5.studymatch.dto.MeResponse;
import upt.group5.studymatch.model.Role;
import upt.group5.studymatch.model.User;
import upt.group5.studymatch.repository.UserRepository;
import upt.group5.studymatch.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // find the user by email
        User user = request.email() == null
                ? null
                : userRepository.findByEmail(request.email()).orElse(null);

        // reject unknown users inactive accounts and wrong passwords
        if (user == null
                || !user.isActive()
                || request.password() == null
                || !passwordEncoder.matches(
                        request.password(), user.getPasswordHash())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of(
                            "message", "Invalid email or password"
                    ));
        }

        // generate a jwt for the authenticated user
        String token = jwtService.generateToken(user);

        // only students have a student id
        Long studentId = null;

        if (user.getRole() == Role.STUDENT && user.getStudent() != null) {
            studentId = user.getStudent().getId();
        }

        // return the login response
        LoginResponse response = new LoginResponse(
                token,
                user.getRole(),
                user.getName(),
                studentId
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {

        // get the email of the authenticated user
        String email = authentication.getName();

        // find the authenticated user in the database
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        // only students have a student id
        Long studentId = null;

        if (user.getRole() == Role.STUDENT && user.getStudent() != null) {
            studentId = user.getStudent().getId();
        }

        // return the authenticated user details
        MeResponse response = new MeResponse(
                user.getName(),
                user.getRole(),
                studentId
        );

        return ResponseEntity.ok(response);
    }

}
