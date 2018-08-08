package com.carsharing.controller;

import com.carsharing.service.MailNotificationService;
import com.carsharing.util.CSResponse;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
class InactiveCarBookingResponse implements Serializable {

    private long idCarBooking;
    private long idUser;
    private String userEmail;
    private long idCar;
    private String carMark;
    private Date beginDate;
    private short countDays;
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
    private MailNotificationService mailNotificationService;

    @GetMapping(value="inactivecars", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех неактивных объявлений об аренде авто",
            response = Car.class, responseContainer = "List"
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

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, inactiveCars), HttpStatus.OK);
    }

    @PutMapping(value="inactivecars", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление (подтверждено/отклонено) определенных объявлений об аренде авто",
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

        List<InactiveCarBookingResponse> inactiveCarBookings = new ArrayList<InactiveCarBookingResponse>();

        for (CarBooking carBooking : carBookings) {

            inactiveCarBookings.add(new InactiveCarBookingResponse(
                    carBooking.getId(),
                    carBooking.getUser().getId(),
                    carBooking.getUser().getEmail(),
                    carBooking.getCar().getId(),
                    carBooking.getCar().getMark(),
                    carBooking.getBeginDate(),
                    carBooking.getCountDays()
            ));
        }

        apiMessage = "Список неактивных броней на аренду авто успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, inactiveCarBookings), HttpStatus.OK);
    }

    @PutMapping(value="inactivecarbookings", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление (подтверждено/отклонено) определенных броней на аренду авто",
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
}
