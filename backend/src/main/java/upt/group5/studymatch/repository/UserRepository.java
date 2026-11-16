package upt.group5.studymatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.group5.studymatch.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}

