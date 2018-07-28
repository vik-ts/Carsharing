package com.carsharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import com.carsharing.model.User;
import com.carsharing.model.UserInfo;
import com.carsharing.repository.UserRepository;
import com.carsharing.repository.UserInfoRepository;

import io.swagger.annotations.*;

class CSResponse<T> {
    private Boolean success;
    private String message;
    private T body;

    public CSResponse(final Boolean success, final String message, @Nullable T body) {
        this.success = success;
        this.message = message;
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public T getBody() {
        return body;
    }
}

@RestController
@Slf4j
@Api
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @PostMapping(value="/registration", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Регистрация пользователя в системе",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Пользователь с таким Email уже существует"),
            @ApiResponse(code = 201, message = "Пользователь успешно зарегистрирован") })
    public ResponseEntity<?> createUser(
            @ApiParam(value = "EMail пользователя", required = true) @RequestBody String email) {

        String apiMessage;

        if (userRepository.findUserByEmail(email) != null) {
            apiMessage = "Пользователь с Email " + email + " уже существует";

            log.warn(apiMessage);
            return new ResponseEntity<>(new CSResponse<>(
                    false, apiMessage,null), HttpStatus.CONFLICT);
        }

        apiMessage = "Пользователь с Email " + email + " успешно создан";
        log.info(apiMessage);
        User user = userRepository.save(new User(email, "1", 1));

        return new ResponseEntity<>(new CSResponse<>(
                true, apiMessage, null), HttpStatus.CREATED);
    }


    @GetMapping(value="userinfo/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение информации о пользователе",
            response = UserInfo.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 302, message = "Информация о пользователе успешно получена") })
    public ResponseEntity<?> getUserInfo(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable Long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";

            log.warn(apiMessage);
            return new ResponseEntity<>(new CSResponse<>(
                            false, apiMessage,null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Информация о пользователе с ID " + id + " успешно получена";

        log.info(apiMessage);
        UserInfo userInfo = user.getUserInfo();

        return new ResponseEntity<>(new CSResponse<>(
                true, apiMessage, userInfo), HttpStatus.FOUND);
    }


    @PutMapping(value="/userinfo/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновлении информации о пользователе",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 302, message = "Информация о пользователе успешно обновлена") })
    public ResponseEntity<?> updateUserInfo(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable Long id,
            @ApiParam(value = "Информация о пользователе", required = true) @RequestBody UserInfo userInfo) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";

            log.warn(apiMessage);
            return new ResponseEntity<>(new CSResponse<>(
                    false, apiMessage,null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Информация о пользователе с ID " + id + " успешно обновлена";

        log.info(apiMessage);
        user.updateUserInfo(userInfo);
        userRepository.save(user);

        return new ResponseEntity<>(new CSResponse<>(
                true, apiMessage, null), HttpStatus.FOUND);
    }


    @DeleteMapping(value="/user/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Удаление пользователя в системе",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 302, message = "Пользователь успешно удален") })
    public ResponseEntity<?> deleteUser(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";

            log.warn(apiMessage);
            return new ResponseEntity<>(new CSResponse<>(
                    false, apiMessage,null), HttpStatus.NOT_FOUND);
        }

        apiMessage = "Пользователь с ID " + id + " успешно удален";

        log.info(apiMessage);
        userRepository.deleteById(id);

        return new ResponseEntity<>(new CSResponse<>(
                true, apiMessage, null), HttpStatus.FOUND);
    }
}
