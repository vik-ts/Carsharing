package com.carsharing.repository;

import org.springframework.data.repository.CrudRepository;

import com.carsharing.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserById(Long id);

}
