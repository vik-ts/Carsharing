package com.carsharing.model;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


@Entity
@Table(name = "user_info")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of={"id"}) @ToString
public class UserInfo implements Serializable {

    @JsonIgnore
    @Id private long id;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "series_number_passport")
    private String seriesNumberPassport;

    @Column(name = "issued_passport")
    private String issuedPassport;

    @Column(name = "issued_date_passport")
    private Date issuedDatePassport;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "series_number_licence")
    private String seriesNumberLicence;

    @Column(name = "issued_license")
    private String issuedLicense;

    @Column(name = "issued_date_license")
    private Date issuedDateLicense;

    @Column(name = "really_license")
    private Date reallyLicense;

    @Column(name = "category")
    private String category;

    @Column(name = "photo", length = 10000000)
    private byte[] photo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;
}
