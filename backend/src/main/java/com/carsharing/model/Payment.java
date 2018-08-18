package com.carsharing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "payment")
@Data
@EqualsAndHashCode(of={"id"}) @ToString
public class Payment implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "payment_requisites")
    private String paymentRequisites;

    @Column(name = "amount_to_pay")
    private Double amountToPay;

    @Column(name = "add_amount_to_pay")
    private Double addAmountToPay;

    @Column(name = "confirmed_user")
    private Boolean confirmedUser;

    @Column(name = "confirmed_admin")
    private Boolean confirmedAdmin;

    @Column(name = "rejected_admin")
    private Boolean rejectedAdmin;

    @Column(name = "closed")
    private Boolean closed;

    @Column(name = "comment")
    private String comment;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private CarBooking carBooking;

    Payment() {
        this.confirmedUser = false;
        this.confirmedAdmin = false;
        this.rejectedAdmin = false;
        this.closed = false;
    }
}