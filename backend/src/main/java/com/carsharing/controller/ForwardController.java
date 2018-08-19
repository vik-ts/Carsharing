package com.carsharing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class ForwardController {

    @RequestMapping(method = RequestMethod.GET, value = {
            "/home", "/signup", "/userinfo", "/login", "/createcar",
            "/activation", "/homeuser", "/usercars", "/editcar/**",
            "/searchcar", "carbooking/**", "/activationbooking",
            "/confirmbooking", "/userbookings", "/caruserbookings",
            "/confirmpayment", "/confirmaddpayment", "/closingpayment",
            "/allpayments"})
    public String redirect() {
        // Forward to home page so that route is preserved.
        return "forward:/index.html";
    }
}
