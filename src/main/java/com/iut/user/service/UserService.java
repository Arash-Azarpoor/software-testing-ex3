package com.iut.user.service;

import com.iut.Repository;
import com.iut.user.model.User;
import com.iut.user.repo.UserRepository;

public class UserService {

    private final Repository<User, String> repository;

    public UserService(UserRepository userRepo) {
        this.repository = userRepo;
    }

    public boolean createUser(User user) {
        if (repository.existsById(user.getId())) {
            return false; 
        }
        return repository.save(user);
    }

    public boolean updateUser(User user) {
        if (repository.existsById(user.getId())) {
            return false;
        }
        return repository.save(user);
    }

    public User getUser(final String id) {
        return repository.findById(id);
    }
}
