package com.zia.auth.service.repository;

import com.zia.auth.service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Boolean existsByName(String name);
}
