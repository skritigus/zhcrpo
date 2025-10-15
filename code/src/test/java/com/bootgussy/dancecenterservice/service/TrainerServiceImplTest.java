package com.bootgussy.dancecenterservice.service;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.repository.TrainerRepository;
import com.bootgussy.dancecenterservice.core.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainerServiceImplTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private CacheConfig cacheConfig;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainer = new Trainer(1L, "Valera", "1234", "Electro", null);
    }

    @Test
    void findTrainerById_ExistingTrainer_ReturnsTrainer() {
        when(cacheConfig.getTrainer(trainer.getId())).thenReturn(null);
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.of(trainer));

        Trainer foundTrainer = trainerService.findTrainerById(trainer.getId());

        assertNotNull(foundTrainer);
        assertEquals(trainer.getId(), foundTrainer.getId());
        verify(cacheConfig).putTrainer(trainer.getId(), trainer);
    }

    @Test
    void findTrainerById_NonExistingTrainer_ThrowsException() {
        when(cacheConfig.getTrainer(trainer.getId())).thenReturn(null);
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.findTrainerById(trainer.getId())
        );

        assertEquals("Trainer not found. ID: 1", exception.getMessage());
    }

    @Test
    void findAllTrainers_ReturnsAllTrainers() {
        when(trainerRepository.findAll()).thenReturn(Collections.singletonList(trainer));

        var trainers = trainerService.findAllTrainers();

        assertNotNull(trainers);
        assertEquals(1, trainers.size());
        assertEquals(trainer, trainers.get(0));
    }

    @Test
    void createTrainer_ValidTrainer_CreatesTrainer() {
        when(trainerRepository.findByNameAndPhoneNumberAndDanceStyle(
                trainer.getName(), trainer.getPhoneNumber(), trainer.getDanceStyle()))
                .thenReturn(Collections.emptyList());
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer createdTrainer = trainerService.createTrainer(trainer);

        assertNotNull(createdTrainer);
        assertEquals(trainer.getId(), createdTrainer.getId());
        verify(cacheConfig).putTrainer(trainer.getId(), createdTrainer);
    }

    @Test
    void createTrainer_TrainerAlreadyExists_ThrowsException() {
        when(trainerRepository.findByNameAndPhoneNumberAndDanceStyle(
                trainer.getName(), trainer.getPhoneNumber(), trainer.getDanceStyle()))
                .thenReturn(Collections.singletonList(trainer));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            trainerService.createTrainer(trainer);
        });

        assertEquals("Trainer already exists. Name: Valera, Phone number: 1234, Dance style: Electro", exception.getMessage());
    }

    @Test
    void createTrainer_NullName_ThrowsException() {
        trainer.setName(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainerService.createTrainer(trainer);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, phoneNumber, danceStyle).", exception.getMessage());
    }

    @Test
    void createTrainer_NullPhoneNumber_ThrowsException() {
        trainer.setPhoneNumber(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainerService.createTrainer(trainer);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, phoneNumber, danceStyle).", exception.getMessage());
    }

    @Test
    void createTrainer_NullDanceStyle_ThrowsException() {
        trainer.setDanceStyle(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainerService.createTrainer(trainer);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, phoneNumber, danceStyle).", exception.getMessage());
    }

    @Test
    void updateTrainer_ValidTrainer_UpdatesTrainer() {
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.of(trainer));
        when(trainerRepository.findByNameAndPhoneNumberAndDanceStyle(
                trainer.getName(), trainer.getPhoneNumber(), trainer.getDanceStyle()))
                .thenReturn(Collections.emptyList());
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer updatedTrainer = trainerService.updateTrainer(trainer);

        assertNotNull(updatedTrainer);
        assertEquals(trainer.getId(), updatedTrainer.getId());
        verify(cacheConfig).putTrainer(trainer.getId(), updatedTrainer);
    }

    @Test
    void updateTrainer_TrainerNotFound_ThrowsException() {
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainerService.updateTrainer(trainer);
        });

        assertEquals("The trainer does not exist. ID: 1, Name: Valera, Phone number: 1234, Dance style: Electro", exception.getMessage());
    }

    @Test
    void updateTrainer_TrainerAlreadyExists_ThrowsException() {
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.of(trainer));
        when(trainerRepository.findByNameAndPhoneNumberAndDanceStyle(
                trainer.getName(), trainer.getPhoneNumber(), trainer.getDanceStyle()))
                .thenReturn(Collections.singletonList(trainer));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            trainerService.updateTrainer(trainer);
        });

        assertEquals("Trainer already exists. Name: Valera, Phone number: 1234, Dance style: Electro", exception.getMessage());
    }

    @Test
    void deleteTrainer_ValidId_DeletesTrainer() {
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.of(trainer));

        trainerService.deleteTrainer(trainer.getId());

        verify(trainerRepository).delete(trainer);
        verify(cacheConfig).removeTrainer(trainer.getId());
    }

    @Test
    void deleteTrainer_NonExistingId_ThrowsException() {
        when(trainerRepository.findById(trainer.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> trainerService.deleteTrainer(trainer.getId())
        );

        assertEquals("Trainer not found. ID: 1", exception.getMessage());
    }
}