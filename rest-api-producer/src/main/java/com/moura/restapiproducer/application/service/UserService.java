package com.moura.restapiproducer.application.service;

import com.moura.restapiproducer.infra.model.User;
import com.moura.restapiproducer.infra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        try {
            return userRepository.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public User getUserByEmail(String email) {
        try {
            Optional<User> u = userRepository.findByEmail(email);
            if (u.isPresent())
                return u.get();

            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public boolean existsUser(String email) {
        try {
            return userRepository.existsById(email);
        } catch (RuntimeException e) {
            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }

    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (RuntimeException e) {
            if (e instanceof DataIntegrityViolationException) {
                throw new RuntimeException("user already registered");
            }

            throw new RuntimeException("internal error: " + e.getMessage());
        }
    }
}
