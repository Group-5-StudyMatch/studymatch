package upt.group5.studymatch.controller;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import upt.group5.studymatch.dto.LoginRequest;
import upt.group5.studymatch.dto.LoginResponse;
import upt.group5.studymatch.model.Role;
import upt.group5.studymatch.model.User;
import upt.group5.studymatch.repository.UserRepository;
import upt.group5.studymatch.security.JwtService;

class AuthControllerTest {

    @Test
    void shouldLoginWithValidCredentials() {

        // create mocks for the controller dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        // create an active test user
        User user = new User(
                "admin@test.com",
                "hashedPassword",
                Role.ADMIN
        );

        user.setId(1L);
        user.setName("Administrador");
        user.setActive(true);

        // configure the mock responses
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("correctPassword", "hashedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("test.jwt.token");

        // create the controller with the mocked dependencies
        AuthController controller = new AuthController(
                userRepository,
                passwordEncoder,
                jwtService
        );

        // execute the login
        ResponseEntity<?> response = controller.login(
                new LoginRequest("admin@test.com", "correctPassword")
        );

        // verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        LoginResponse body = assertInstanceOf(
                LoginResponse.class,
                response.getBody()
        );

        assertNotNull(body.token());
        assertEquals("test.jwt.token", body.token());
        assertEquals(Role.ADMIN, body.role());
        assertEquals("Administrador", body.name());
        assertEquals(null, body.studentId());
    }

    @Test
    void shouldRejectIncorrectPassword() {

        // create mocks for the controller dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        // create an active user with a stored password hash
        User user = new User(
                "admin@test.com",
                "hashedPassword",
                Role.ADMIN
        );

        user.setId(1L);
        user.setActive(true);

        // simulate an existing user
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(user));

        // simulate an incorrect password
        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        // create the controller
        AuthController controller = new AuthController(
                userRepository,
                passwordEncoder,
                jwtService
        );

        // attempt to log in with the wrong password
        ResponseEntity<?> response = controller.login(
                new LoginRequest("admin@test.com", "wrongPassword")
        );

        // verify that the login is rejected
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // verify that no jwt is generated
        org.mockito.Mockito.verify(
                jwtService,
                org.mockito.Mockito.never()
        ).generateToken(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void shouldRejectUnknownEmail() {

        // create mocks for the controller dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        // simulate an email that does not exist
        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        // create the controller
        AuthController controller = new AuthController(
                userRepository,
                passwordEncoder,
                jwtService
        );

        // attempt to log in with an unknown email
        ResponseEntity<?> response = controller.login(
                new LoginRequest("unknown@test.com", "somePassword")
        );

        // verify that the login is rejected
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // verify that no password is checked
        org.mockito.Mockito.verifyNoInteractions(passwordEncoder);

        // verify that no jwt is generated
        org.mockito.Mockito.verifyNoInteractions(jwtService);
    }

    @Test
    void shouldRejectInactiveUser() {

        // create mocks for the controller dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        // create an inactive test user
        User user = new User(
                "inactive@test.com",
                "hashedPassword",
                Role.ADMIN
        );

        user.setId(2L);
        user.setActive(false);

        // simulate an existing but inactive user
        when(userRepository.findByEmail("inactive@test.com"))
                .thenReturn(Optional.of(user));

        // create the controller
        AuthController controller = new AuthController(
                userRepository,
                passwordEncoder,
                jwtService
        );

        // attempt to log in with an inactive account
        ResponseEntity<?> response = controller.login(
                new LoginRequest("inactive@test.com", "correctPassword")
        );

        // verify that the login is rejected
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // verify that the password is not checked
        org.mockito.Mockito.verifyNoInteractions(passwordEncoder);

        // verify that no jwt is generated
        org.mockito.Mockito.verifyNoInteractions(jwtService);
    }

    @Test
    void shouldReturnAuthenticatedUserDetails() {

        // create mocks for the controller dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        // create an authenticated test user
        User user = new User(
                "admin@test.com",
                "hashedPassword",
                Role.ADMIN
        );

        user.setId(1L);
        user.setName("Administrador");
        user.setActive(true);

        // simulate finding the authenticated user
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(user));

        // create the controller
        AuthController controller = new AuthController(
                userRepository,
                passwordEncoder,
                jwtService
        );

        // simulate an authenticated request
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com",
                        null
                );

        // request the authenticated user details
        ResponseEntity<upt.group5.studymatch.dto.MeResponse> response =
                controller.me(authentication);

        // verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        upt.group5.studymatch.dto.MeResponse body =
                assertInstanceOf(
                        upt.group5.studymatch.dto.MeResponse.class,
                        response.getBody()
                );

        assertEquals("Administrador", body.name());
        assertEquals(Role.ADMIN, body.role());
        assertEquals(null, body.studentId());
    }




}
