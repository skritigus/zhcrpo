package com.bootgussy.dancecenterservice.core.repository;

import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
}
