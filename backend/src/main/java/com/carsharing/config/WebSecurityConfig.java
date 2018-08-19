package com.carsharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.carsharing.security.AuthUserDetailsService;
import com.carsharing.security.AuthEntryPoint;
import com.carsharing.security.JwtTokenAuthFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configurable
@EnableWebSecurity
// Modifying or overriding the default spring boot security.
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthUserDetailsService authUserDetailsService;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    // This method is for overriding the default AuthenticationManagerBuilder.
    // We can specify how the user details are kept in the application. It may
    // be in a database, LDAP or in memory.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtTokenAuthFilter jwtTokenAuthFilterBean() throws Exception {
        return new JwtTokenAuthFilter();
    }

    // This method is for overriding some configuration of the WebSecurity
    // If you want to ignore some request or request patterns then you can
    // specify that inside this method
    @Override
    public void configure(WebSecurity web) throws Exception {
        //for Swagger
        web.ignoring().antMatchers(
                "/webjars/**",
                "/v2/api-docs/**",
                "/configuration/ui/**",
                "/configuration/security/**",
                "/swagger-resources/**",
                "/swagger-ui.html/**",
                "/swagger-ui.html#/**");

        //for Angular5 frontend
        web.ignoring().antMatchers(HttpMethod.GET,
                "/", "/index.html", "/favicon.ico", "/*.js",
                "/home", "/signup", "/userinfo", "/login", "/createcar",
                "/activation", "/homeuser", "/usercars", "/editcar/**",
                "/searchcar", "carbooking/**", "/activationbooking",
                "/confirmbooking", "/userbookings", "/caruserbookings",
                "/confirmpayment", "/confirmaddpayment", "/closingpayment",
                "/allpayments");
    }

    // This method is used for override HttpSecurity of the web Application.
    // We can specify our authorization criteria inside this method.
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                // set custom auth entry point
                .exceptionHandling().authenticationEntryPoint(authEntryPoint).and()

                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()

                .antMatchers(HttpMethod.POST, "/registration", "/login").permitAll()

                .anyRequest().authenticated().and()

                .addFilterBefore(jwtTokenAuthFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
}
