package com.bootgussy.dancecenterservice.service;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.repository.HallRepository;
import com.bootgussy.dancecenterservice.core.service.impl.HallServiceImpl;
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

class HallServiceImplTest {
    @InjectMocks
    private HallServiceImpl hallService;

    @Mock
    private HallRepository hallRepository;

    @Mock
    private CacheConfig cacheConfig;

    private Hall hall;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hall = new Hall(1L, "Main Hall", 200, null);
    }

    @Test
    void findHallById_ExistingHall_ReturnsHall() {
        when(cacheConfig.getHall(hall.getId())).thenReturn(null);
        when(hallRepository.findById(hall.getId())).thenReturn(Optional.of(hall));

        Hall foundHall = hallService.findHallById(hall.getId());

        assertNotNull(foundHall);
        assertEquals(hall.getId(), foundHall.getId());
        verify(cacheConfig).putHall(hall.getId(), hall);
    }

    @Test
    void findHallById_NonExistingHall_ThrowsException() {
        when(cacheConfig.getHall(hall.getId())).thenReturn(null);
        when(hallRepository.findById(hall.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> hallService.findHallById(hall.getId())
        );

        assertEquals("Hall not found. ID: 1", exception.getMessage());
    }

    @Test
    void findAllHalls_ReturnsAllHalls() {
        when(hallRepository.findAll()).thenReturn(Collections.singletonList(hall));

        var halls = hallService.findAllHalls();

        assertNotNull(halls);
        assertEquals(1, halls.size());
        assertEquals(hall, halls.get(0));
    }

    @Test
    void createHall_ValidHall_CreatesHall() {
        when(hallRepository.findByName(hall.getName())).thenReturn(Optional.empty());
        when(hallRepository.save(hall)).thenReturn(hall);

        Hall createdHall = hallService.createHall(hall);

        assertNotNull(createdHall);
        assertEquals(hall.getId(), createdHall.getId());
        verify(cacheConfig).putHall(hall.getId(), createdHall);
    }

    @Test
    void createHall_HallAlreadyExists_ThrowsException() {
        when(hallRepository.findByName(hall.getName())).thenReturn(Optional.of(hall));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            hallService.createHall(hall);
        });

        assertEquals("Hall already exists. Name: Main Hall, Area: 200", exception.getMessage());
    }

    @Test
    void createHall_InvalidHall_ThrowsException() {
        hall.setName(null); // Устанавливаем имя в null

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            hallService.createHall(hall);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, area).", exception.getMessage());
    }

    @Test
    void createHall_InvalidHallArea_ThrowsException() {
        hall.setArea(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            hallService.createHall(hall);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, area).", exception.getMessage());
    }

    @Test
    void updateHall_ValidHall_UpdatesHall() {
        when(hallRepository.findById(hall.getId())).thenReturn(Optional.of(hall));
        when(hallRepository.findByName(hall.getName())).thenReturn(Optional.empty());
        when(hallRepository.save(hall)).thenReturn(hall);

        Hall updatedHall = hallService.updateHall(hall);

        assertNotNull(updatedHall);
        assertEquals(hall.getId(), updatedHall.getId());
        verify(cacheConfig).putHall(hall.getId(), updatedHall);
    }

    @Test
    void updateHall_HallNotFound_ThrowsException() {
        when(hallRepository.findById(hall.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            hallService.updateHall(hall);
        });

        assertEquals("The hall does not exist. ID: 1, Name: Main HallArea: 200", exception.getMessage());
    }

    @Test
    void updateHall_HallAlreadyExists_ThrowsException() {
        Hall anotherHall = new Hall(2L, "Main Hall", 300, null);
        when(hallRepository.findById(hall.getId())).thenReturn(Optional.of(hall));
        when(hallRepository.findByName(hall.getName())).thenReturn(Optional.of(anotherHall));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            hallService.updateHall(hall);
        });

        assertEquals("Hall already exists. Name: Main Hall, Area: 200", exception.getMessage());
    }

    @Test
    void deleteHall_ValidId_DeletesHall() {
        when(hallRepository.findById(hall.getId())).thenReturn(Optional.of(hall));

        hallService.deleteHall(hall.getId());

        verify(hallRepository).delete(hall);
        verify(cacheConfig).removeHall(hall.getId());
    }

    @Test
    void deleteHall_NonExistingId_ThrowsException() {
        when(hallRepository.findById(hall.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            hallService.deleteHall(hall.getId());
        });

        assertEquals("Hall not found. ID: 1", exception.getMessage());
    }

    @Test
    void createHall_NullArea_ThrowsException() {
        hall.setArea(null); // Устанавливаем площадь в null

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> hallService.createHall(hall)
        );

        assertEquals("Incorrect JSON. All fields must be filled (name, area).", exception.getMessage());
    }

    @Test
    void updateHall_NullName_ThrowsException() {
        hall.setName(null); // Устанавливаем имя в null

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            hallService.updateHall(hall);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, area).", exception.getMessage());
    }

    @Test
    void updateHall_NullArea_ThrowsException() {
        hall.setArea(null); // Устанавливаем площадь в null

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            hallService.updateHall(hall);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, area).", exception.getMessage());
    }
}