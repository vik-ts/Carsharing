package com.carsharing.controller;

import com.carsharing.service.MailNotificationService;
import com.carsharing.util.CSResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import lombok.Data;


@Data
class UpdateInactiveCar implements Serializable {

    private long id;
    private Boolean active;
    private Boolean reject;
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
    private MailNotificationService mailNotificationService;

    @GetMapping(value="inactivecars", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех неактивных объявлений об аренде авто",
            response = Car.class, responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список объявлений об аренде авто успешно получен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getInactiveCars() {

        String apiMessage;

        List<Car> inactiveCars = carRepository.findCarsByActiveFalseAndRejectFalse();

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
            @RequestBody List<UpdateInactiveCar> updateInactiveCars
    ) {

        for (UpdateInactiveCar updateInactiveCar : updateInactiveCars) {
            Car car = carRepository.findCarById(updateInactiveCar.getId());

            if (car != null) {
                car.setActive(updateInactiveCar.getActive());
                car.setReject(updateInactiveCar.getReject());
                car.setComment(updateInactiveCar.getComment());

                carRepository.save(car);

                mailNotificationService.sendDecisionByCar(
                        car.getUser().getEmail(),
                        car.getActive(),
                        car.getReject(),
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
}
