package upt.group5.studymatch.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import upt.group5.studymatch.model.Role;
import upt.group5.studymatch.model.User;
import upt.group5.studymatch.repository.UserRepository;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // only create an admin if there are no users yet
        if (userRepository.count() > 0) {
            return;
        }

        // get the admin credentials from environment variables
        String email = System.getenv("ADMIN_EMAIL");
        String password = System.getenv("ADMIN_PASSWORD");

        // skip admin creation if email or password is missing
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            logger.warn(
                "Initial admin not created: " +
                "ADMIN_EMAIL or ADMIN_PASSWORD is missing."
            );
            return;
        }

        // hash the password before saving it
        String passwordHash = passwordEncoder.encode(password);

        // create the admin account
        User admin = new User(email.trim(), passwordHash, Role.ADMIN);

        // save the admin in the database
        userRepository.save(admin);

        logger.info("Initial admin account created successfully.");
    }
}
