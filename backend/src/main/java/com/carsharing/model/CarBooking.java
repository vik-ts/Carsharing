package com.carsharing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "car_booking")
@Data
@EqualsAndHashCode(of={"id"}) @ToString(exclude={"id", "user", "car"})
public class CarBooking implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "begin_date") @NotNull
    private Date beginDate;

    @Column(name = "count_days") @NotNull
    private short countDays;

    @Column(name = "activated")
    private Boolean activated;

    @Column(name = "confirmed")
    private Boolean confirmed;

    @Column(name = "rejected")
    private Boolean rejected;

    @Column(name = "canceled")
    private Boolean canceled;

    @Column(name = "comment")
    private String comment;

    public CarBooking(User user, Car car, Date beginDate, short countDays) {
        this.user = user;
        this.car = car;
        this.beginDate = beginDate;
        this.countDays = countDays;
        this.activated = false;
        this.confirmed = false;
        this.rejected = false;
        this.canceled = false;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;
}