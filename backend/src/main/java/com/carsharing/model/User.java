package com.carsharing.model;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.mapping.Set;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@Data
//@NoArgsConstructor
@EqualsAndHashCode(of=("id")) @ToString(exclude={"cars", "carBookings", "userInfo"})
public class User implements UserDetails, Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id private long id;

    @Column(name = "email") /*@Email*/ @NotNull @Size(max = 65)
    @NonNull private String email;

    @JsonIgnore
    @Column(name = "password") @NotNull @Size(max = 128)
    @NonNull private String password;

    @Column(name = "role") @NotNull
    private long role;

    public User() {}

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @NonNull private UserInfo userInfo;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Car> cars;

    public void addCar(Car car) {
        car.setUser(this);

        for (Calendar calendar : car.getCalendar()) {
            calendar.setCar(car);
        }

        cars.add(car);
    }

    public void removeCar(Car car) {
        cars.remove(car);
        car.setUser(null);
    }

    public void updateCar(Car car) {
        int indexCarForUpdate = cars.indexOf(car);

        if (indexCarForUpdate < 0) {
            addCar(car);
        } else {
            Car updCar = cars.get(indexCarForUpdate);

            for (Calendar calendar : car.getCalendar()) {
                calendar.setCar(updCar);
            }

            BeanUtils.copyProperties(car, updCar, "id", "user", "carBookings", "calendar");

            updCar.getCalendar().clear();
            updCar.getCalendar().addAll(car.getCalendar());
        }
    }

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CarBooking> carBookings;

    public User(String email, String password, long role) {
        this.email = email;
        this.setPassword(password); //with encoding
        this.role = role;
        this.setUserInfo(new UserInfo());
    }

    private void setUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            if (this.userInfo != null) {
                this.userInfo.setUser(null);
            }
        }
        else {
            userInfo.setUser(this);
        }
        this.userInfo = userInfo;
    }

    public void updateUserInfo(UserInfo userInfo) {
        if ((userInfo != null) && (this.userInfo != null)) {
            BeanUtils.copyProperties(userInfo, this.userInfo, "id", "user");
        }
    }

    public void setPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    //implementations of abstract methods of the UserDetails class
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role == 0 ? "ADMIN" : "USER"));
        return authorities;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
