package com.bootgussy.dancecenterservice.core.repository;

import com.bootgussy.dancecenterservice.core.model.Role;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
