package com.carsharing.controller;

import com.carsharing.repository.CarRepository;
import com.carsharing.repository.UserRepository;
import com.carsharing.security.AuthController;
import com.carsharing.service.SearchCarsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@WebMvcTest(value = CarController.class, secure=false)
public class CarControllerTest {

    //MockMvc is the main entry point for server-side Spring MVC test support.
    //It allows us to execute requests against the test context.
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CarRepository carRepository;

    @MockBean
    SearchCarsService searchCarsService;

    @Test
    public void createCar() throws Exception {
    }

    @Test
    public void getCar() {
    }

    @Test
    public void updateCar() {
    }

    @Test
    public void deleteCar() {
    }

    @Test
    public void getUserCars() {
    }

    @Test
    public void searchCars() {
    }
}