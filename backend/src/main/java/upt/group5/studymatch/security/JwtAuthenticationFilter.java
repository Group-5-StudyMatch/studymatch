package upt.group5.studymatch.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import upt.group5.studymatch.model.User;
import upt.group5.studymatch.repository.UserRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //class that alows execute filter once for request

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // get the authorization header
        String authHeader = request.getHeader("Authorization");

        // continue without authentication if there is no bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // validate the jwt and get the user email
            Claims claims = jwtService.validateToken(token);
            String email = claims.getSubject();

            if (email != null && !email.isBlank()
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                // find the user associated with the token
                Optional<User> userOptional =
                        userRepository.findByEmail(email);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    // only authenticate active users
                    if (user.isActive()) {

                        // convert the user role into a spring security authority
                        SimpleGrantedAuthority authority =
                                new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name()
                                );

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getEmail(),
                                        null,
                                        List.of(authority)
                                );

                        // store the authentication for the current request
                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            }

        } catch (JwtException | IllegalArgumentException ex) {
            // clear authentication when the token is invalid
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
