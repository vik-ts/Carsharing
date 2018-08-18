package com.carsharing.controller;

import com.carsharing.model.Car;
import com.carsharing.model.CarBooking;
import com.carsharing.model.User;
import com.carsharing.repository.CarBookingRepository;
import com.carsharing.repository.PaymentRepository;
import com.carsharing.repository.UserRepository;
import com.carsharing.util.CSResponse;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.carsharing.model.Payment;


@Data
class UnconfirmedPaymentRequestForUser implements Serializable {

    private long id;
    private String paymentRequisites;
    private Double addAmountToPay;
    private String comment;
}

@Data
class UnclosedPaymentRequest implements Serializable {

    private long id;
}

@Data
class PaymentResponse implements Serializable {

    private long id;
    private long idCarUser;
    private String emailCarUser;
    private long idUser;
    private String emailUser;
    private String mark;
    private Double price;
    private Date beginDate;
    private Short countDays;
    private Date returnDate;

    private String paymentRequisites;
    private Double amountToPay;
    private Double addAmountToPay;
    private Boolean confirmedUser;
    private Boolean confirmedAdmin;
    private Boolean rejectedAdmin;
    private Boolean closed;
    private String comment;

    PaymentResponse(Payment payment) {
        CarBooking carBooking = payment.getCarBooking();
        BeanUtils.copyProperties(carBooking, this);

        Car car = carBooking.getCar();
        BeanUtils.copyProperties(car, this);

        User user = car.getUser();
        this.idCarUser = user.getId();
        this.emailCarUser = user.getEmail();

        user = carBooking.getUser();
        this.idUser = user.getId();
        this.emailUser = user.getEmail();

        BeanUtils.copyProperties(payment, this);
    }
}

@RestController
@Slf4j
@Api
public class PaymentController {
    @Autowired
    CarBookingRepository carBookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @PostMapping(value="/returncar/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Возврат авто после аренды и создание платежа на оплату",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Бронь не найдена"),
            @ApiResponse(code = 200, message = "Возврат авто успешно выполнен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> returnCar(
            @ApiParam(value = "ID брони", required = true) @PathVariable long id) {

        String apiMessage;

        CarBooking carBooking = carBookingRepository.findCarBookingById(id);

        if (carBooking == null) {
            apiMessage = "Бронь с ID " + id + " не найдена";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        carBooking.setReturnDate(new Date());
        carBooking.addPayment();
        carBookingRepository.save(carBooking);

        apiMessage = "Возврат авто успешно выполнен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    private Specification<Payment> GetUnconfirmedPaymentsForCarUser(User user) {
        return (root, query, builder) ->
                builder.and(builder.equal(root.get("carBooking").get("car").get("user"), user),
                            builder.isFalse(root.get("confirmedUser")));
    }

    @GetMapping(value = "confirmpayment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех неподтвержденных платежей после возврата авто",
            response = PaymentResponse.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Список неподтвержденных оплат успешно получен")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getUnconfirmedPayments(
            @ApiParam(value = "ID пользователя (арендодателя)", required = true)
            @PathVariable long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        List<Payment> payments = paymentRepository.findAll(GetUnconfirmedPaymentsForCarUser(user));

        List<PaymentResponse> unconfirmedPayments = new ArrayList<>();

        for (Payment payment : payments) {
            unconfirmedPayments.add(new PaymentResponse(payment));
        }

        apiMessage = "Список неподтвержденных платежей успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, unconfirmedPayments), HttpStatus.OK);
    }


    @PutMapping(value="confirmpayment", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление (подтверждено/отклонено) определенных платежей после возврата авто",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список объявлений об аренде авто успешно обновлен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateUnconfirmaedPayments(
            @ApiParam(value = "Список объявлений об аренде авто для обновления", required = true)
            @RequestBody List<UnconfirmedPaymentRequestForUser> uncorfirmedPayments
    ) {

        for (UnconfirmedPaymentRequestForUser unconfirmedPayment : uncorfirmedPayments) {
            Payment payment = paymentRepository.findPaymentById(unconfirmedPayment.getId());

            if (payment != null) {
                payment.setAddAmountToPay(unconfirmedPayment.getAddAmountToPay());
                payment.setComment(unconfirmedPayment.getComment());
                payment.setPaymentRequisites(unconfirmedPayment.getPaymentRequisites());
                payment.setConfirmedUser(true);

                if (payment.getAmountToPay() == 0) {
                    payment.setConfirmedAdmin(true);
                }
                payment.setRejectedAdmin(false);

                paymentRepository.save(payment);
            }
        }

        String apiMessage;

        apiMessage = "Список неподтвержденных платежей успешно обновлен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    private Specification<Payment> GetActivePaymentsForUser(User user) {
        return (root, query, builder) ->
                builder.and(builder.equal(root.get("carBooking").get("user"), user),
                            builder.isTrue(root.get("confirmedUser")),
                            builder.isTrue(root.get("confirmedAdmin")),
                            builder.isFalse(root.get("closed")));
    }

    @GetMapping(value = "activepayment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Незакрытие платежи, выставленные арендатору за аренду авто",
            response = PaymentResponse.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Список незакрытых платежей успешно получен")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getUnclosedPayments(
            @ApiParam(value = "ID пользователя (арендатора)", required = true)
            @PathVariable long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        List<PaymentResponse> payments = new ArrayList<>();

        List<Payment> paymentsForUser = paymentRepository.findAll(GetActivePaymentsForUser(user));

        for (Payment payment : paymentsForUser) {
            payments.add(new PaymentResponse(payment));
        }

        apiMessage = "Список незакрытых платежей успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, payments), HttpStatus.OK);
    }

    @PutMapping(value="activepayment", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Закрытие(оплата) незакрытых платежей на аренду авто",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список платежей успешно закрыт") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateUnclosedPayments(
            @ApiParam(value = "Список объявлений об аренде авто для обновления", required = true)
            @RequestBody List<UnclosedPaymentRequest> unclosedPayments
    ) {

        for (UnclosedPaymentRequest uncloseddPayment : unclosedPayments) {
            Payment payment = paymentRepository.findPaymentById(uncloseddPayment.getId());

            if (payment != null) {
                payment.setClosed(true);

                paymentRepository.save(payment);
            }
        }

        String apiMessage;

        apiMessage = "Список неподтвержденных оплат успешно обновлен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    private Specification<Payment> GetPaymentsForCarUser(User user) {
        return (root, query, builder) ->
                builder.equal(root.get("carBooking").get("car").get("user"), user);
    }

    private Specification<Payment> GetPaymentsForUser(User user) {
        return (root, query, builder) ->
                builder.equal(root.get("carBooking").get("user"), user);
    }

    @GetMapping(value = "allpayments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cписок всех платежей для участника (арендатора/арендодателя)",
            response = PaymentResponse.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Список всех платежей успешно получен")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getAllPayments(
            @ApiParam(value = "ID пользователя", required = true)
            @PathVariable long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        List<PaymentResponse> payments = new ArrayList<>();

        List<Payment> paymentsForCarUser = paymentRepository.findAll(GetPaymentsForCarUser(user));

        for (Payment payment : paymentsForCarUser) {
            payments.add(new PaymentResponse(payment));
        }

        List<Payment> paymentsForUser = paymentRepository.findAll(GetPaymentsForUser(user));

        for (Payment payment : paymentsForUser) {
            payments.add(new PaymentResponse(payment));
        }

        apiMessage = "Список всех платежей успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, payments), HttpStatus.OK);
    }
}