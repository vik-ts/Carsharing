package com.carsharing.controller;

import com.carsharing.model.CarBooking;
import com.carsharing.repository.CarBookingRepository;
import com.carsharing.service.MailNotificationService;
import com.carsharing.util.CSResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import com.carsharing.model.User;
import com.carsharing.model.Car;
import com.carsharing.repository.UserRepository;
import com.carsharing.repository.CarRepository;

import io.swagger.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.Data;


@Data
class CarBookingRequest implements Serializable {

    private long idUser;
    private long idCar;
    private Date beginDate;
    private short countDays;
}

@Data
class UnconfirmedCarBookingRequest implements Serializable {

    private long id;
    private Boolean confirmed;
    private Boolean rejected;
    private String comment;
}

@Data
class CarBookingResponse implements Serializable {

    private long idCarBooking;
    private long idUser;
    private String userEmail;
    private long idCar;
    private Double price;
    private String carMark;
    private Date beginDate;
    private short countDays;
    private Date returnDate;
    private Boolean activated;
    private Boolean confirmed;
    private Boolean rejected;
    private Boolean canceled;
    private String comment;

    CarBookingResponse(CarBooking carBooking) {
        this.idCarBooking = carBooking.getId();
        this.idUser = carBooking.getUser().getId();
        this.userEmail = carBooking.getUser().getEmail();
        this.idCar = carBooking.getCar().getId();
        this.carMark = carBooking.getCar().getMark();
        this.price = carBooking.getCar().getPrice();

        BeanUtils.copyProperties(carBooking, this);
    }
}


@RestController
@Slf4j
@Api
public class CarBookingController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarBookingRepository carBookingRepository;

    @Autowired
    private MailNotificationService mailNotificationService;

    @PostMapping(value = "booking", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Создание брони на аренду авто арендатором",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь или объявление об аренде авто не найдены"),
            @ApiResponse(code = 200, message = "Бронь на аренду авто успешно создана")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> createCarBooking(@RequestBody CarBookingRequest carBookingRequest) {

        String apiMessage;

        long idUser = carBookingRequest.getIdUser();
        User user = userRepository.findUserById(idUser);

        if (user == null) {
            apiMessage = "Пользователь с ID " + idUser + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        long idCar = carBookingRequest.getIdCar();
        Car car = carRepository.findCarById(idCar);

        if (car == null) {
            apiMessage = "Объявление об аренде авто с ID " + idCar + " не найдено";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        CarBooking carBooking = new CarBooking(user, car,
                carBookingRequest.getBeginDate(),
                carBookingRequest.getCountDays()
        );
        carBookingRepository.save(carBooking);

        apiMessage = "Бронь на аренду авто успешно создана";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }

    private Specification<CarBooking> GetUnconfirmedCarBookingForCarUser(User user) {
        return (root, query, builder) ->
                builder.and(builder.equal(root.get("car").get("user"), user),
                            builder.isTrue(root.get("activated")),
                            builder.isFalse(root.get("rejected")),
                            builder.isFalse(root.get("confirmed")),
                            builder.isFalse(root.get("canceled")));
    }

    @GetMapping(value = "confirmbooking/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех неподтвержденных броней на аренду авто для арендодателя",
            response = CarBookingResponse.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Список неподтвержденных броней на аренду авто успешно получен")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getUnconfirmedCarBookings(
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

        List<CarBooking> CarBookings =
                carBookingRepository.findAll(GetUnconfirmedCarBookingForCarUser(user));

        List<CarBookingResponse> unconfirmedCarBookings = new ArrayList<>();

        for (CarBooking carBooking : CarBookings) {
            unconfirmedCarBookings.add(new CarBookingResponse(carBooking));
        }

        apiMessage = "Список неподтвержденных броней на аренду авто успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, unconfirmedCarBookings), HttpStatus.OK);
    }

    @PutMapping(value = "confirmbooking", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Подтверждение брони на аренду авто арендадателем",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список броней на аренду авто успешно обновлен")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateUnconfirmedCarBookings(
            @ApiParam(value = "Список объявлений об аренде авто для обновления", required = true)
            @RequestBody List<UnconfirmedCarBookingRequest> UnconfirmedCarBookings
    ) {
        for (UnconfirmedCarBookingRequest unconfirmedCarBooking : UnconfirmedCarBookings) {
            CarBooking carBooking = carBookingRepository.findCarBookingById(unconfirmedCarBooking.getId());

            if (carBooking != null) {
                carBooking.setConfirmed(unconfirmedCarBooking.getConfirmed());
                carBooking.setRejected(unconfirmedCarBooking.getRejected());
                carBooking.setComment(unconfirmedCarBooking.getComment());

                carBookingRepository.save(carBooking);

                mailNotificationService.sendDecisionByCarBookingFromCarOwner(
                        carBooking.getUser().getEmail(),
                        carBooking.getConfirmed(),
                        carBooking.getRejected(),
                        carBooking.getComment()
                );
            }
        }

        String apiMessage;

        apiMessage = "Список неподтвержденных броней на аренду авто успешно обновлен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }

    @PutMapping(value = "cancelbooking/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Аннулирование брони на аренду авто",
            response = CSResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "До начала периода аренды осталось менее 2 часов. Бронь на аренду авто не может быть аннулирована."),
            @ApiResponse(code = 404, message = "Бронь на аренду авто не найдена"),
            @ApiResponse(code = 200, message = "Бронь на аренду авто успешно аннулирована")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> cancelCarBooking(
            @ApiParam(value = "ID брони на аренду авто", required = true)
            @PathVariable long id) {

        String apiMessage;

        CarBooking carBooking = carBookingRepository.findCarBookingById(id);

        if (carBooking == null) {
            apiMessage = "Бронь на аренду авто с ID " + id + " не найдена";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        Calendar cal = Calendar.getInstance();

        if ((carBooking.getBeginDate().getTime() - cal.getTimeInMillis()) / (60 * 60 * 1000) % 24 < 2) {
            apiMessage = "До начала периода аренды осталось менее 2 часов. Бронь на аренду авто не может быть аннулирована.";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.CONFLICT);
        }

        carBooking.setCanceled(true);
        carBookingRepository.save(carBooking);

        apiMessage = "Бронь на аренду авто успешно аннулирована";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }

    @GetMapping(value = "userbookings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех броней на аренду авто для арендатора",
            response = CarBookingResponse.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Список броней успешно получен")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getUserCarBookings(
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

        apiMessage = "Список броней на аренду авто для арендатора успешно получен";
        log.info(apiMessage);

        List<CarBooking> userCarBookings = user.getCarBookings();

        List<CarBookingResponse> carBookings = new ArrayList<>();

        for (CarBooking carBooking : userCarBookings) {
            carBookings.add(new CarBookingResponse(carBooking));
        }

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, carBookings), HttpStatus.OK);
    }

    @GetMapping(value = "caruserbookings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех броней на аренду авто для арендодателя",
            response = CarBookingResponse.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Список броней успешно получен")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getCarUserCarBookings(
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

        apiMessage = "Список броней на аренду авто для арендодателя успешно получен";
        log.info(apiMessage);

        List<Car> userCars = user.getCars();

        List<CarBookingResponse> carBookings = new ArrayList<>();

        for (Car car : userCars) {
            List<CarBooking> userCarBookings = car.getCarBookings();

            for (CarBooking carBooking : userCarBookings) {
                carBookings.add(new CarBookingResponse(carBooking));
            }
        }

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, carBookings), HttpStatus.OK);
    }
}
