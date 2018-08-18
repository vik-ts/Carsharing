package com.carsharing.controller;

import com.carsharing.controller.DTO.UserInfoDTO;
import com.carsharing.util.CSResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import com.carsharing.model.User;
import com.carsharing.repository.UserRepository;

import io.swagger.annotations.*;


@RestController
@Slf4j
@Api
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(value="userinfo/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение информации о пользователе",
            response = UserInfoDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Информация о пользователе успешно получена") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> getUserInfo(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                            apiMessage, null), HttpStatus.NOT_FOUND);
        }

        UserInfoDTO userInfo = new UserInfoDTO(user.getUserInfo());

        apiMessage = "Информация о пользователе с ID " + id + " успешно получена";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, userInfo), HttpStatus.OK);
    }


    @PutMapping(value="/userinfo/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление информации о пользователе",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Информация о пользователе успешно обновлена") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateUserInfo(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable long id,
            @ApiParam(value = "Информация о пользователе", required = true) @RequestBody UserInfoDTO userInfoDTO) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage,null), HttpStatus.NOT_FOUND);
        }

        user.updateUserInfo(userInfoDTO);
        userRepository.save(user);

        apiMessage = "Информация о пользователе с ID " + id + " успешно обновлена";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    @DeleteMapping(value="/user/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Удаление пользователя в системе",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Пользователь успешно удален") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> deleteUser(
            @ApiParam(value = "ID пользователя", required = true) @PathVariable long id) {

        String apiMessage;

        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(id);

        apiMessage = "Пользователь с ID " + id + " успешно удален";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }
}
