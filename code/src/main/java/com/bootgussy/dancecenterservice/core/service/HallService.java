package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.core.model.Hall;
import java.util.List;

public interface HallService {
    Hall findHallById(Long id);

    List<Hall> findAllHalls();

    Hall createHall(Hall hall);

    Hall updateHall(Hall hall);

    void deleteHall(Long id);
}
