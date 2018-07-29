package com.carsharing.model;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(exclude={"id"}) @ToString(exclude={"id"})
//@NoArgsConstructor
//@RequiredArgsConstructor
public class User implements UserDetails, Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id private long id;

    @Column(name = "email") /*@Email*/ @NotNull @Size(max = 65)
    @NonNull private String email;

    @JsonIgnore
    @Column(name = "password") @NotNull @Size(max = 128)
    @NonNull private String password;

    @Column(name = "role") @NotNull
    @NonNull private long role;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @NonNull private UserInfo userInfo;

    public User() { }

    public User(String email, String password, long role) {
        this.email = email;
        this.password = password;
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

    //implementations of abstract methods of UserDetails class
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
