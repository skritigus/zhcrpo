package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.repository.TrainerRepository;
import com.bootgussy.dancecenterservice.core.service.TrainerService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final CacheConfig cacheConfig;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository,
                              CacheConfig cacheConfig) {
        this.trainerRepository = trainerRepository;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Trainer findTrainerById(Long id) {
        Trainer cachedTrainer = cacheConfig.getTrainer(id);
        if (cachedTrainer != null) {
            return cachedTrainer;
        }

        Trainer trainer = trainerRepository.findById(id).orElse(null);

        if (trainer != null) {
            cacheConfig.putTrainer(id, trainer);

            return trainer;
        } else {
            throw new ResourceNotFoundException("Trainer not found. ID: " + id);
        }
    }

    @Override
    public List<Trainer> findAllTrainers() {
        return trainerRepository.findAll();
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        Trainer savedTrainer;

        if (
                trainer.getUser().getName() == null ||
                        trainer.getUser().getPhoneNumber() == null ||
                        trainer.getDanceStyle() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(name, phoneNumber, danceStyle).");
        }

        if (trainerRepository.findByNameAndPhoneNumberAndDanceStyle(
                        trainer.getUser().getName(),
                        trainer.getUser().getPhoneNumber(),
                        trainer.getDanceStyle()
                )
                .isEmpty()) {
            savedTrainer = trainerRepository.save(trainer);
        } else {
            throw new AlreadyExistsException("Trainer already exists. " +
                    "Name: " + trainer.getUser().getName() +
                    ", Phone number: " + trainer.getUser().getPhoneNumber() +
                    ", Dance style: " + trainer.getDanceStyle());
        }

        cacheConfig.putTrainer(savedTrainer.getId(), savedTrainer);

        return savedTrainer;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Trainer updatedTrainer;

        if (
                trainer.getUser().getName() == null ||
                        trainer.getUser().getPhoneNumber() == null ||
                        trainer.getDanceStyle() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(name, phoneNumber, danceStyle).");
        }

        if (!trainerRepository.findByNameAndPhoneNumberAndDanceStyle(
                        trainer.getUser().getName(),
                        trainer.getUser().getPhoneNumber(),
                        trainer.getDanceStyle()
                )
                .isEmpty()) {
            throw new AlreadyExistsException("Trainer already exists. " +
                    "Name: " + trainer.getUser().getName() +
                    ", Phone number: " + trainer.getUser().getPhoneNumber() +
                    ", Dance style: " + trainer.getDanceStyle());
        }

        if (trainerRepository.findById(trainer.getId()).isPresent()) {
            updatedTrainer = trainerRepository.save(trainer);
        } else {
            throw new ResourceNotFoundException("The trainer does not exist. " +
                    "ID: " + trainer.getId() +
                    ", Name: " + trainer.getUser().getName() +
                    ", Phone number: " + trainer.getUser().getPhoneNumber() +
                    ", Dance style: " + trainer.getDanceStyle());
        }

        cacheConfig.putTrainer(updatedTrainer.getId(), updatedTrainer);

        return updatedTrainer;
    }

    @Override
    public void deleteTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found. ID: " + id));

        cacheConfig.removeTrainer(trainer.getId());

        trainerRepository.delete(trainer);
    }
}