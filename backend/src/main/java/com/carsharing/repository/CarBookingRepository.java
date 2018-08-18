package com.carsharing.repository;

import com.carsharing.model.CarBooking;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarBookingRepository extends CrudRepository<CarBooking, Long>, JpaSpecificationExecutor<CarBooking> {
    CarBooking findCarBookingById(Long id);
    List<CarBooking> findCarBookingsByActivatedFalseAndRejectedFalseAndCanceledFalse();
}

