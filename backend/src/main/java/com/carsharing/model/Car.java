package com.carsharing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "car")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of={"id"}) @ToString(exclude={"user", "carBookings", "calendar"})
public class Car implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "activated")
    private Boolean activated;

    @Column(name = "rejected")
    private Boolean rejected;

    @Column(name = "comment")
    private String comment;

    @Column(name = "mark") @NotNull
    private String mark;

    @Column(name = "model") @NotNull
    private String model;

    @Column(name = "year_issued") @NotNull
    private short yearIssued;

    @Column(name = "state_number") @NotNull
    private String stateNumber;

    @Column(name = "mileage")
    private int mileage;

    @Column(name = "seats_number")
    private short seatsNumber;

    @Column(name = "location")
    private String location;

    @Column(name = "gearbox_type")
    private String gearboxType;

    @Column(name = "body_type")
    private String bodyType;

    @Column(name = "drive")
    private String drive;

    @Column(name = "engine_type") @NotNull
    private String engineType;

    @Column(name = "fuel_type") @NotNull
    private String fuelType;

    @Column(name = "fuel_consumption")
    private Double fuelConsumption;

    @Column(name = "state")
    private String state;

    @Column(name = "accessories")
    private String accessories;

    @Column(name = "insurance")
    private String insurance;

    @Column(name = "price") @NotNull
    private double price;

    @Column(name = "ad_text", length = 500) @NotNull
    private String adText;

    @OneToMany(
            mappedBy = "car",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Calendar> calendar;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(
            mappedBy = "car",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CarBooking> carBookings;
}
