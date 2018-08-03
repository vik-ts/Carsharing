package com.carsharing.repository;

import org.springframework.data.repository.CrudRepository;

import com.carsharing.model.Car;

import java.util.List;

public interface CarRepository extends CrudRepository<Car, Long> {
    Car findCarById(Long id);
    List<Car> findCarsByActiveFalseAndRejectFalse();
}
