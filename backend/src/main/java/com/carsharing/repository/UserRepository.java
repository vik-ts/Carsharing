package com.carsharing.repository;

import com.carsharing.model.CarBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.carsharing.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserById(Long id);
}
