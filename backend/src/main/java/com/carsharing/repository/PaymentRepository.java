package com.carsharing.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

import com.carsharing.model.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long>, JpaSpecificationExecutor<Payment>  {
    Payment findPaymentById(long id);
    List<Payment> findPaymentsByConfirmedAdminFalseAndRejectedAdminFalseAndConfirmedUserTrue();
}
