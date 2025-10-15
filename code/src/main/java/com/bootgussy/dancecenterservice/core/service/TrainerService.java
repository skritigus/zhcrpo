package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.core.model.Trainer;
import java.util.List;

public interface TrainerService {
    Trainer findTrainerById(Long id);

    List<Trainer> findAllTrainers();

    Trainer createTrainer(Trainer trainer);

    Trainer updateTrainer(Trainer trainer);

    void deleteTrainer(Long id);
}
