package com.carsharing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Table(name = "car_photo")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of={"id"}) @ToString(exclude={"car"})
public class CarPhoto {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Id private long id;

    @Column(name = "photo", length = 10000000)
    private byte[] photo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private Car car;
}
