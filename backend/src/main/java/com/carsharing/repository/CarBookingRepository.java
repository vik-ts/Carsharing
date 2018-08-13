package com.carsharing.repository;

import com.carsharing.model.CarBooking;
import com.carsharing.model.Payment;
import com.carsharing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import org.springframework.data.repository.CrudRepository;

import java.util.List;
import com.carsharing.model.Car;

public interface CarBookingRepository extends CrudRepository<CarBooking, Long>, JpaSpecificationExecutor<CarBooking> {
    CarBooking findCarBookingById(Long id);
    List<CarBooking> findCarBookingsByActivatedFalseAndRejectedFalseAndCanceledFalse();
}

