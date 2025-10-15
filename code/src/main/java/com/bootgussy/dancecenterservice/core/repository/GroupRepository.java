package com.bootgussy.dancecenterservice.core.repository;

import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByTrainerAndDifficulty(Trainer trainer, String difficulty);

    @Query("SELECT g FROM Group g WHERE g.trainer.danceStyle = :danceStyle")
    List<Group> findAllByDanceStyle(@Param("danceStyle") String danceStyle);
}
