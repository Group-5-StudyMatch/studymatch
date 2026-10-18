package upt.group5.studymatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import upt.group5.studymatch.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(String name);

}

