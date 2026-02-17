package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.core.model.User;

public interface UserService {

    User findByName(String name);
}
