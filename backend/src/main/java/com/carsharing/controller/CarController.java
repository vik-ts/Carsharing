package com.carsharing.controller;

import com.carsharing.service.SearchCarsService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

@RestController
@Slf4j
@Api
public class CarController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    SearchCarsService searchCarsService;

    @PostMapping(value="/car/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Создание объвления об аренде авто",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Объявление об аренде авто успешно создано") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> createCar(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable long id,
            @ApiParam(value = "Объявление об аренде авто", required = true) @RequestBody Car car) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Объявление об аренде авто успешно создано";
        log.info(apiMessage);

        car.setActivated(false);
        car.setRejected(false);
        car.setComment("");
        user.addCar(car);
        userRepository.save(user);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    @GetMapping(value="car/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение объявления об аренде авто",
            response = Car.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Объявление об аренде авто не найдено"),
            @ApiResponse(code = 200, message = "Объявления об аренде авто успешно получено") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getCar(
            @ApiParam(value = "ID объявления об аренде авто", required = true) @PathVariable long id) {

        String apiMessage;

        Car car = carRepository.findCarById(id);

        if (car == null) {
            apiMessage = "Объявление об аренде авто с ID " + id + " не найдено";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Информация об аренде авто успешно получена";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, car), HttpStatus.OK);
    }

    @PutMapping(value="/car/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление объявления об аренде авто",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Объявление об аренде авто не найдено"),
            @ApiResponse(code = 200, message = "Объявления об аренде авто успешно обновлено") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateCar(
            @ApiParam(value = "ID объявления об аренде авто", required = true) @PathVariable long id,
            @ApiParam(value = "Объявление об аренде авто", required = true) @RequestBody Car car) {

        String apiMessage;

        Car updateCar = carRepository.findCarById(id);

        if (updateCar == null) {
            apiMessage = "Объявление об аренде авто c ID " + id + " не найдено";

            log.warn(apiMessage);
            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Объявление об аренде авто с успешно обновлено";
        log.info(apiMessage);

        car.setActivated(false);
        car.setRejected(false);
        updateCar.getUser().updateCar(car);
        carRepository.save(updateCar);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    @DeleteMapping(value="/car/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Удаление объявления об аренде авто",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Объявление об аренде авто не найдено"),
            @ApiResponse(code = 200, message = "Объявления об аренде авто успешно удалено") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> deleteCar(
            @ApiParam(value = "ID объявления об аренде авто", required = true) @PathVariable long id) {

        String apiMessage;

        Car deleteCar = carRepository.findCarById(id);

        if (deleteCar == null) {
            apiMessage = "Объявление об аренде авто c ID " + id + " не найдено";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Объявление об аренде авто успешно удалено";
        log.info(apiMessage);

        User user = userRepository.findUserById(deleteCar.getUser().getId());
        user.removeCar(deleteCar);
        userRepository.save(user);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    @GetMapping(value="/usercars/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение списка всех объявлений об аренде авто конкретного пользователя",
            response = Car.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Список объявлений об аренде авто успешно получен") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getUserCars(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Список объявлений об аренде авто для пользователя с ID " + id + " успешно получен";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, user.getCars()), HttpStatus.OK);
    }


    @GetMapping(value="/searchcars", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Поиск объявлений об аренде авто",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Поиск объявлений об аренде авто успешно выполнен") })
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Номер страницы с результатом (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Количество записей на странице"),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Сортировка задается в виде: имя_поля(,asc|desc). Поддерживается сортировка по нескольким полям.")
    })
    public ResponseEntity<?> searchCars(Pageable pageRequest,
            @RequestParam(required = false) String mark,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Short lowYearIssued,
            @RequestParam(required = false) Short highYearIssued,
            @RequestParam(required = false) Integer lowMileage,
            @RequestParam(required = false) Integer highMileage,
            @RequestParam(required = false) Short seatsNumber,
            @RequestParam(required = false) String gearboxType,
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) String drive,
            @RequestParam(required = false) String engineType,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) Double lowPrice,
            @RequestParam(required = false) Double highPrice,
            @RequestParam(required = false) Date rentBeginDate,
            @RequestParam(required = false) Short rentCountDays) {

        Page<Car> foundCars = searchCarsService.find(pageRequest, mark, model, lowYearIssued, highYearIssued,
                lowMileage, highMileage, seatsNumber, gearboxType, bodyType, drive, engineType, fuelType,
                lowPrice, highPrice, rentBeginDate, rentCountDays);

        String apiMessage = "Поиск объявлений об аренде авто успешно выполнен";

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, foundCars), HttpStatus.OK);
    }
}
