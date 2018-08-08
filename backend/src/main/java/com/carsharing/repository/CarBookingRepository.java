package com.carsharing.repository;

import com.carsharing.model.CarBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.carsharing.model.Car;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarBookingRepository extends CrudRepository<CarBooking, Long> {
    CarBooking findCarBookingById(Long id);
    List<CarBooking> findCarBookingsByActivatedFalseAndRejectedFalseAndCanceledFalse();
    List<CarBooking> findCarBookingsByActivatedTrueAndRejectedFalseAndCanceledFalse();
}

