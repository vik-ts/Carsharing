package com.carsharing.controller;

import com.carsharing.controller.DTO.CarDTO;
import com.carsharing.model.Payment;
import com.carsharing.repository.PaymentRepository;
import com.carsharing.service.MailNotificationService;
import com.carsharing.util.CSResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import com.carsharing.model.User;
import com.carsharing.model.Car;
import com.carsharing.model.CarBooking;
import com.carsharing.repository.UserRepository;
import com.carsharing.repository.CarRepository;
import com.carsharing.repository.CarBookingRepository;

import io.swagger.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;


@Data
class InactiveCarRequest implements Serializable {

    private long id;
    private Boolean activated;
    private Boolean rejected;
    private String comment;
}

@Data
class InactiveCarBookingRequest implements Serializable {

    private long id;
    private Boolean activated;
    private Boolean rejected;
    private String comment;
}

@Data
class InactiveCarBookingResponse implements Serializable {

    private long idCarBooking;
    private long idUser;
    private String userEmail;
    private long idCar;
    private String carMark;
    private Date beginDate;
    private short countDays;
    private Boolean activated;
    private Boolean rejected;
    private String comment;

    InactiveCarBookingResponse(CarBooking carBooking) {

        this.idCarBooking = carBooking.getId();
        this.idUser = carBooking.getUser().getId();
        this.userEmail = carBooking.getUser().getEmail();
        this.idCar = carBooking.getCar().getId();
        this.carMark = carBooking.getCar().getMark();

        BeanUtils.copyProperties(carBooking, this);
    }
}

@Data
class UnconfirmedPaymentResponseForAdmin implements Serializable {

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

    UnconfirmedPaymentResponseForAdmin(Payment payment) {
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

@Data
class UnconfirmedPaymentRequestForAdmin implements Serializable {

    private long id;
    private Boolean confirmedAdmin;
    private Boolean rejectedAdmin;
    private String comment;
}

@RestController
@Slf4j
@Api
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarBookingRepository carBookingRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    private MailNotificationService mailNotificationService;

    @GetMapping(value="inactivecars", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех неактивных объявлений об аренде авто",
            response = CarDTO.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список неактивных объявлений об аренде авто успешно получен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getInactiveCars() {

        String apiMessage;

        List<Car> inactiveCars = carRepository.findCarsByActivatedFalseAndRejectedFalse();

        apiMessage = "Список неактивных объявлений об аренде авто успешно получен";
        log.info(apiMessage);

        List<CarDTO> carsDTO = inactiveCars.stream().map(CarDTO::new).collect(Collectors.toList());

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, carsDTO), HttpStatus.OK);
    }

    @PutMapping(value="inactivecars", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление (активировано/отклонено) определенных объявлений об аренде авто",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список объявлений об аренде авто успешно обновлен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateInactiveCars(
            @ApiParam(value = "Список объявлений об аренде авто для обновления", required = true)
            @RequestBody List<InactiveCarRequest> inactiveCars
    ) {

        for (InactiveCarRequest inactiveCar : inactiveCars) {
            Car car = carRepository.findCarById(inactiveCar.getId());

            if (car != null) {
                car.setActivated(inactiveCar.getActivated());
                car.setRejected(inactiveCar.getRejected());
                car.setComment(inactiveCar.getComment());

                carRepository.save(car);

                mailNotificationService.sendDecisionByCar(
                        car.getUser().getEmail(),
                        car.getActivated(),
                        car.getRejected(),
                        car.getComment()
                );
            }
        }

        String apiMessage;

        apiMessage = "Список неактивных объявлений об аренде авто успешно обновлен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }

    @GetMapping(value="inactivecarbookings", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех неактивных броней на аренду авто",
            response = InactiveCarBookingResponse.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список неактивных броней на аренду авто успешно получен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getInactiveCarBookings() {

        String apiMessage;

        List<CarBooking> carBookings = carBookingRepository.findCarBookingsByActivatedFalseAndRejectedFalseAndCanceledFalse();

        List<InactiveCarBookingResponse> inactiveCarBookings = new ArrayList<>();

        for (CarBooking carBooking : carBookings) {
            inactiveCarBookings.add(new InactiveCarBookingResponse(carBooking));
        }

        apiMessage = "Список неактивных броней на аренду авто успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, inactiveCarBookings), HttpStatus.OK);
    }

    @PutMapping(value="inactivecarbookings", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление (активировано/отклонено) определенных броней на аренду авто",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список броней на аренду авто успешно обновлен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateInactiveCarBookings(
            @ApiParam(value = "Список броней на аренду авто для обновления", required = true)
            @RequestBody List<InactiveCarBookingRequest> inactiveCarBookings
    ) {

        for (InactiveCarBookingRequest inactiveCarBooking : inactiveCarBookings) {
            CarBooking carBooking = carBookingRepository.findCarBookingById(inactiveCarBooking.getId());

            if (carBooking != null) {
                carBooking.setActivated(inactiveCarBooking.getActivated());
                carBooking.setRejected(inactiveCarBooking.getRejected());
                carBooking.setComment(inactiveCarBooking.getComment());

                carBookingRepository.save(carBooking);

                mailNotificationService.sendDecisionByCarBookingFromAdmin(
                        carBooking.getUser().getEmail(),
                        carBooking.getCar().getUser().getEmail(),
                        carBooking.getActivated(),
                        carBooking.getRejected(),
                        carBooking.getComment()
                );
            }
        }

        String apiMessage;

        apiMessage = "Список неактивных броней на аренду авто успешно обновлен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }

    @GetMapping(value="unconfirmedpayments", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех неподтвержденных платежей с допоплатой",
            response = UnconfirmedPaymentResponseForAdmin.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список неподтвержденных платежей с допоплатой") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getUnconfirmedPaymentsWithAddAmount() {

        String apiMessage;

        List<Payment> payments =
                paymentRepository.findPaymentsByConfirmedAdminFalseAndRejectedAdminFalseAndConfirmedUserTrue();

        List<UnconfirmedPaymentResponseForAdmin> unconfirmedPayments = new ArrayList<>();

        for (Payment payment : payments) {
            unconfirmedPayments.add(new UnconfirmedPaymentResponseForAdmin(payment));
        }

        apiMessage = "Список неподтвержденных платежей с допоплатой успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, unconfirmedPayments), HttpStatus.OK);
    }

    @PutMapping(value="unconfirmedpayments", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление (подтверждено/отклонено) определенных платежей с допоплатой",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список платежей с допоплатой успешно обновлен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateUnconfirmedPayments(
            @ApiParam(value = "Список платежей с допоплатой для обновления", required = true)
            @RequestBody List<UnconfirmedPaymentRequestForAdmin> unconfirmedPayments
    ) {

        for (UnconfirmedPaymentRequestForAdmin unconfirmedPayment : unconfirmedPayments) {
            Payment payment = paymentRepository.findPaymentById(unconfirmedPayment.getId());

            if (payment != null) {
                payment.setConfirmedAdmin(unconfirmedPayment.getConfirmedAdmin());
                payment.setRejectedAdmin(unconfirmedPayment.getRejectedAdmin());
                payment.setComment(unconfirmedPayment.getComment());

                if (payment.getRejectedAdmin()) {
                    payment.setConfirmedUser(false);
                }

                paymentRepository.save(payment);
            }
        }

        String apiMessage;

        apiMessage = "Список платежей с допоплатой успешно обновлен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }
}
