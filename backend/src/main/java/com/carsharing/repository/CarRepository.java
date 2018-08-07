package com.carsharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.carsharing.model.Car;
import java.util.List;

//import org.springframework.stereotype.Repository;
//@Repository

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    Car findCarById(Long id);
    List<Car> findCarsByActiveFalseAndRejectFalse();
}
