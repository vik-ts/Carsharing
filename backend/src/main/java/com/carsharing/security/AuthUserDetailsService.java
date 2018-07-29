package com.carsharing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.carsharing.repository.UserRepository;
import com.carsharing.model.User;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

/**
 * This Service class for providing the user credentials from the database.
 *
 * @author ITBird
 *
 */

@Service
@Primary
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }

        return user;
    }
}
