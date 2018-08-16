package com.carsharing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "calendar")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of={"id"}) @ToString
public class Calendar implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Id private long id;

    private Date beginDate;
    private Short countDays;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;
}
