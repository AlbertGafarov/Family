package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gafarov.Family.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
