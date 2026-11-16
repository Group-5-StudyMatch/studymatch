package upt.group5.studymatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //cong do spring
public class PasswordConfig {

    @Bean //spring creates an object PasswordEncoder for other classes to use
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  //implementation that will use hashes BCrypt
    }
}
