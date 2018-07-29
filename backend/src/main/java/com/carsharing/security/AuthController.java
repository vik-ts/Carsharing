package com.carsharing.security;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.*;

import com.carsharing.model.User;

@Data
class AuthRequest implements Serializable {

    private String email;
    private String password;

    public AuthRequest() {
        super();
    }
}

@Data
@AllArgsConstructor
class AuthResponse implements Serializable {

    private long idUser;
    private String token;
}

class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
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

    @ApiOperation(value = "Вход пользователя в систему",
            response = AuthResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь успешно вошел в систему") })
    @PostMapping(value = "/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {

        authenticate(authRequest.getEmail(), authRequest.getPassword());

        // Reload password post-security so we can generate the token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        final long idUser = ((User)userDetails).getId();

        // Return the token
        return new ResponseEntity<>(new AuthResponse(idUser, token), HttpStatus.OK);
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad credentials!", e);
        }
    }
}
