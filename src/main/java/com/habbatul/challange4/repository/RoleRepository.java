package com.habbatul.challange4.repository;

import com.habbatul.challange4.entity.security.Roles;
import com.habbatul.challange4.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRoleName(ERole name);

}
