package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Role;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.model.User;
import com.bootgussy.dancecenterservice.core.repository.RoleRepository;
import com.bootgussy.dancecenterservice.core.repository.TrainerRepository;
import com.bootgussy.dancecenterservice.core.repository.UserRepository;
import com.bootgussy.dancecenterservice.core.service.TrainerService;
import java.util.List;
import java.util.Collections;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheConfig cacheConfig;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository,
                              UserRepository userRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder,
                              CacheConfig cacheConfig) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    @Transactional
    public List<Trainer> findAllTrainers() {
        // Sync any users who have the role but no entity
        List<User> trainersWithRole = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("TRAINER")))
                .toList();

        for (User user : trainersWithRole) {
            if (trainerRepository.findByUserId(user.getId()).isEmpty()) {
                Trainer trainer = new Trainer();
                trainer.setUser(user);
                trainer.setDanceStyle("Not specified");
                trainerRepository.save(trainer);
            }
        }

        return trainerRepository.findAll().stream()
                .filter(t -> t.getUser().getRoles().stream().anyMatch(r -> r.getName().equals("TRAINER")))
                .toList();
    }

    @Override
    @Transactional
    public Trainer createTrainer(Trainer trainer) {
        if (trainer.getUser() == null || trainer.getDanceStyle() == null) {
            throw new ResourceNotFoundException("Incorrect data: User information and dance style are required.");
        }

        User userData = trainer.getUser();
        String phone = userData.getPhoneNumber();

        // Find existing user or create new one
        User user = userRepository.findByPhoneNumber(phone).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhoneNumber(phone);
            newUser.setName(userData.getName());
            if (userData.getPassword() != null) {
                newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
            }
            return newUser;
        });

        // Ensure TRAINER role exists
        Role trainerRole = roleRepository.findByName("TRAINER")
                .orElseThrow(() -> new ResourceNotFoundException("Role TRAINER not found"));
        
        if (user.getRoles() == null) {
            user.setRoles(new java.util.ArrayList<>(Collections.singletonList(trainerRole)));
        } else if (!user.getRoles().contains(trainerRole)) {
            user.getRoles().add(trainerRole);
        }

        userRepository.save(user);
        trainer.setUser(user);

        if (trainerRepository.findByUserId(user.getId()).isPresent()) {
            throw new AlreadyExistsException("Trainer record already exists for this user.");
        }

        Trainer savedTrainer = trainerRepository.save(trainer);
        cacheConfig.putTrainer(savedTrainer.getId(), savedTrainer);

        return savedTrainer;
    }

    @Override
    @Transactional
    public Trainer updateTrainer(Trainer trainer) {
        Trainer existingTrainer = trainerRepository.findById(trainer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + trainer.getId()));

        User user = existingTrainer.getUser();
        user.setName(trainer.getUser().getName());
        user.setPhoneNumber(trainer.getUser().getPhoneNumber());
        
        if (trainer.getUser().getPassword() != null && !trainer.getUser().getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(trainer.getUser().getPassword()));
        }

        existingTrainer.setDanceStyle(trainer.getDanceStyle());

        Trainer updated = trainerRepository.save(existingTrainer);
        cacheConfig.putTrainer(updated.getId(), updated);
        return updated;
    }

    @Override
    @Transactional
    public void deleteTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found. ID: " + id));

        User user = trainer.getUser();
        if (user != null) {
            Role trainerRole = roleRepository.findByName("TRAINER").orElse(null);
            if (trainerRole != null && user.getRoles() != null) {
                user.getRoles().remove(trainerRole);
                userRepository.save(user);
            }
        }

        cacheConfig.removeTrainer(trainer.getId());
        trainerRepository.delete(trainer);
    }

    @Override
    @Transactional
    public Trainer findTrainerByUserId(Long userId) {
        return trainerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    if (user.getRoles().stream().anyMatch(r -> r.getName().equals("TRAINER"))) {
                        Trainer t = new Trainer();
                        t.setUser(user);
                        t.setDanceStyle("Not specified");
                        return trainerRepository.save(t);
                    }
                    throw new ResourceNotFoundException("Trainer entity not found and user doesn't have Trainer role");
                });
    }
}