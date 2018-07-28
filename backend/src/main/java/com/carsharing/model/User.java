package com.carsharing.model;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;


@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(exclude={"id"}) @ToString(exclude={"id"})
//@NoArgsConstructor
//@RequiredArgsConstructor
public class User implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id private long id;

    @Column(name = "email") /*@Email*/ @NotNull @Size(max = 65)
    @NonNull private String email;

    @JsonIgnore
    @Column(name = "password") @NotNull @Size(max = 128)
    @NonNull private String password;

    @Column(name = "role") @NotNull
    @NonNull private Integer role;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @NonNull private UserInfo userInfo;

    public User() { }

    public User(String email, String password, Integer role) {
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
}
