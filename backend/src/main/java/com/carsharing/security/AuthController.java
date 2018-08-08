package com.carsharing.security;

import java.io.Serializable;
import java.util.Random;

import com.carsharing.service.MailNotificationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.*;

import com.carsharing.model.User;
import com.carsharing.util.CSResponse;
import com.carsharing.repository.UserRepository;

@Data
class AuthRequest implements Serializable {

    private String email;
    private String password;
}

@Data
@AllArgsConstructor
class AuthResponse implements Serializable {

    private long idUser;
    private String email;
    private Boolean isAdmin;
    private String token;
}

@RestController
@Slf4j
@Api
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailNotificationService mailNotificationService;

    private static String generatePassword(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    @PostMapping(value="/registration", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Регистрация пользователя в системе",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Пользователь с таким Email уже существует"),
            @ApiResponse(code = 200, message = "Пользователь успешно зарегистрирован") })
    public ResponseEntity<?> createUser(
            @ApiParam(value = "EMail пользователя", required = true) @RequestBody String email) {

        String apiMessage;

        if (userRepository.findUserByEmail(email) != null) {
            apiMessage = "Пользователь с Email " + email + " уже существует";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.CONFLICT);
        }

        String password = "1"; //generatePassword(5);

        userRepository.save(new User(email, password, 1));

        apiMessage = "Пользователь с Email " + email + " успешно создан, пароль - " + password;
        log.info(apiMessage);

        mailNotificationService.sendPassword(email, password);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }


    @PostMapping(value = "/login")
    @ApiOperation(value = "Вход пользователя в систему",
            response = AuthResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь успешно вошел в систему") })
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {

        String apiMessage;

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(), authRequest.getPassword()));

        // Reload password post-security so we can generate the token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        final User user = (User)userDetails;
        final long idUser = user.getId();
        final String email = user.getEmail();
        final Boolean isAdmin = ((User)userDetails).getRole() == 0;
        final String token = jwtTokenUtil.generateToken(userDetails, idUser);

        apiMessage = "Пользователь успешно вошел в систему";
        log.info(apiMessage);

        // Return the token
        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, new AuthResponse(idUser, email, isAdmin, token)), HttpStatus.OK);
    }


    @PutMapping(value="/login/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление регистрационных данных пользователя",
            response = CSResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 409, message = "Пользователь с таким Email уже существует"),
            @ApiResponse(code = 200, message = "Обновление регистрационных данных пользователя выполнено успешно") })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-token", value = "Токен для доступа к методу", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<?> updateLoginUser(
            @ApiParam(value = "ID пользователя", required = true)
                @PathVariable long id,
            @ApiParam(value = "Новые Email и пароль пользователя (обрабатываются непустые значения)", required = true)
                @RequestBody AuthRequest authRequest) {

        String apiMessage;
        User user = userRepository.findUserById(id);

        if (user == null) {
            apiMessage = "Пользователь с ID " + id + " не найден";
            log.warn(apiMessage);

            return new ResponseEntity<>(new CSResponse<>(
                    apiMessage, null), HttpStatus.NOT_FOUND);
        }

        String changedEmail = authRequest.getEmail();

        if (!changedEmail.equals("")) {
            if (!user.getEmail().equals(changedEmail)) {
                if (userRepository.findUserByEmail(changedEmail) != null) {
                    apiMessage = "Пользователь с Email " + changedEmail + " уже существует";
                    log.warn(apiMessage);

                    return new ResponseEntity<>(new CSResponse<>(
                            apiMessage, null), HttpStatus.CONFLICT);
                }
                user.setEmail(changedEmail);
            }
        }

        if (!authRequest.getPassword().equals("")) {
            user.setPassword(authRequest.getPassword());
        }

        userRepository.save(user);

        apiMessage = "Обновление регистрационных данных пользователя выполнено успешно";
        log.info(apiMessage);

        return new ResponseEntity<>(new CSResponse<>(
                apiMessage, null), HttpStatus.OK);
    }
}
