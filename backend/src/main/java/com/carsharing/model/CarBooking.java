package com.carsharing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;


@Entity
@Table(name = "car_booking")
@Data
@EqualsAndHashCode(of={"id"}) @ToString
public class CarBooking implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "begin_date") @NotNull
    private Date beginDate;

    @Column(name = "count_days") @NotNull
    private short countDays;

    @Column(name = "return_date")
    private Date returnDate;

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

    public CarBooking() {}

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

    @OneToOne(
            mappedBy = "carBooking",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Payment payment;

    public void addPayment() {
        this.payment = new Payment();
        this.payment.setCarBooking(this);

        Double amountToPay = this.car.getPrice() * this.countDays;
        amountToPay = new BigDecimal(amountToPay).setScale(2, RoundingMode.HALF_UP).doubleValue();

        Double addAmountToPay = 0.0;
        long MILLISECONDS_PER_DAY = (24 * 60 * 60 * 1000);

        long diff = this.returnDate.getTime() - (this.beginDate.getTime() + this.countDays * MILLISECONDS_PER_DAY);

        if (diff > 0) {
            addAmountToPay = this.car.getPrice() * diff / MILLISECONDS_PER_DAY;
            addAmountToPay = new BigDecimal(addAmountToPay).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        this.payment.setAmountToPay(amountToPay);
        this.payment.setAddAmountToPay(addAmountToPay);
    }
}