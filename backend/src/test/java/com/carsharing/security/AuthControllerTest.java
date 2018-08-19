package com.carsharing.security;

import com.carsharing.model.User;
import com.carsharing.repository.UserRepository;
import com.carsharing.service.MailNotificationService;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringRunner.class)
@WebMvcTest(value = AuthController.class, secure=false)
public class AuthControllerTest {

    //MockMvc is the main entry point for server-side Spring MVC test support.
    //It allows us to execute requests against the test context.
    @Autowired
    private MockMvc mockMvc;

    //MockBean is used to add mocks to a Spring ApplicationContext.
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailNotificationService mailNotificationService;

    @Test
    public void createUser() throws Exception {
        String testData = "test@test.com";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/registration")
                .accept(MediaType.APPLICATION_JSON).content(testData)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void loginUser() throws Exception {
        User mockUser = new User("test@test.com","1", 0);

        // userDetailsService.loadUserByUsername to respond back with mockUser
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any(String.class))).thenReturn(mockUser);

        String testData = "{\"email\":\"test@test.com\",\"password\":\"1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/login")
                .accept(MediaType.APPLICATION_JSON).content(testData)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void updateLoginUser() throws Exception {
        User mockUser = new User("test11@test.com","11", 0);

        // userRepository.findUserById to respond back with mockUser
        Mockito.when(userRepository.findUserById(Mockito.eq(88888L))).thenReturn(mockUser);

        String testData = "{\"email\":\"test@test.com\",\"password\":\"1\"}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/login/88888")
                .accept(MediaType.APPLICATION_JSON).content(testData)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}