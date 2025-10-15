package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.repository.HallRepository;
import com.bootgussy.dancecenterservice.core.service.HallService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HallServiceImpl implements HallService {
    private final HallRepository hallRepository;
    private final CacheConfig cacheConfig;

    @Autowired
    public HallServiceImpl(HallRepository hallRepository,
                           CacheConfig cacheConfig) {
        this.hallRepository = hallRepository;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Hall findHallById(Long id) {
        Hall cachedHall = cacheConfig.getHall(id);
        if (cachedHall != null) {
            return cachedHall;
        }

        Hall hall = hallRepository.findById(id).orElse(null);

        if (hall != null) {
            cacheConfig.putHall(id, hall);

            return hall;
        } else {
            throw new ResourceNotFoundException("Hall not found. ID: " + id);
        }
    }

    @Override
    public List<Hall> findAllHalls() {
        return hallRepository.findAll();
    }

    @Override
    public Hall createHall(Hall hall) {
        Hall savedHall;

        if (
                hall.getName() == null ||
                        hall.getArea() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled (name, area).");
        }

        if (hallRepository.findByName(hall.getName()).isEmpty()) {
            savedHall = hallRepository.save(hall);
        } else {
            throw new AlreadyExistsException("Hall already exists." +
                    " Name: " + hall.getName() +
                    ", Area: " + hall.getArea());
        }
        cacheConfig.putHall(savedHall.getId(), savedHall);

        return savedHall;
    }

    @Override
    public Hall updateHall(Hall hall) {
        Hall updatedHall;

        if (
                hall.getName() == null ||
                        hall.getArea() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled (name, area).");
        }

        Optional<Hall> searchedHall = hallRepository.findByName(hall.getName());

        if (searchedHall.isPresent() && !hall.getId().equals(searchedHall.get().getId())) {
            throw new AlreadyExistsException("Hall already exists." +
                    " Name: " + hall.getName() +
                    ", Area: " + hall.getArea());
        }

        if (hallRepository.findById(hall.getId()).isPresent()) {
            updatedHall = hallRepository.save(hall);
        } else {
            throw new ResourceNotFoundException("The hall does not exist." +
                    " ID: " + hall.getId() +
                    ", Name: " + hall.getName() +
                    "Area: " + hall.getArea());
        }

        cacheConfig.putHall(updatedHall.getId(), updatedHall);

        return updatedHall;
    }

    @Override
    public void deleteHall(Long id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found. ID: " + id));

        cacheConfig.removeHall(hall.getId());

        hallRepository.delete(hall);
    }
}
