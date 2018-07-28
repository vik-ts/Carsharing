package com.carsharing.model;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Table(name = "user_info")
@Data
@EqualsAndHashCode(exclude={"id"}) @ToString (exclude={"id"})
@NoArgsConstructor @RequiredArgsConstructor
public class UserInfo implements Serializable {

    @JsonIgnore
    @Id private long id;

    @Column(name = "fullname")
    @NonNull private String fullname;

    @Column(name = "telephone")
    @NonNull private String telephone;

    @Column(name = "series_number_passport")
    @NonNull private String seriesNumberPassport;

    @Column(name = "issued_passport")
    @NonNull private String issuedPassport;

    @Column(name = "issued_date_passport")
    @NonNull private Date issuedDatePassport;

    @Column(name = "birthday")
    @NonNull private Date birthday;

    @Column(name = "birth_place")
    @NonNull private String birthPlace;

    @Column(name = "series_number_licence")
    @NonNull private String seriesNumberLicence;

    @Column(name = "issued_license")
    @NonNull private String issuedLicense;

    @Column(name = "issued_date_license")
    @NonNull private Date issuedDateLicense;

    @Column(name = "really_license")
    @NonNull private Date reallyLicense;

    @Column(name = "category")
    @NonNull private String category;

    @Column(name = "photo")
    private byte[] photo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;
}
