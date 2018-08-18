package com.carsharing.controller.DTO;

import com.carsharing.model.Calendar;
import com.carsharing.model.Car;
import com.carsharing.model.CarPhoto;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Base64Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CarDTO implements Serializable {

    private long id;
    private Boolean activated;
    private Boolean rejected;
    private String comment;
    private String mark;
    private String model;
    private Short yearIssued;
    private String stateNumber;
    private Integer mileage;
    private Short seatsNumber;
    private String location;
    private String gearboxType;
    private String bodyType;
    private String drive;
    private String engineType;
    private String fuelType;
    private Double fuelConsumption;
    private String state;
    private String accessories;
    private String insurance;
    private Double price;
    private String adText;
    private List<Calendar> calendar = new ArrayList<>();
    private List<String> carPhotos = new ArrayList<>();

    public CarDTO(Car car) {
        BeanUtils.copyProperties(car, this, "calendar", "carPhotos");

        this.calendar.clear();
        this.calendar.addAll(car.getCalendar());

        this.carPhotos.clear();
        for (CarPhoto carPhoto : car.getCarPhotos()) {
            this.carPhotos.add(Base64Utils.encodeToString(carPhoto.getPhoto()));
        }
    }

    public static void CopyPropFromCarDTOToCar(CarDTO carDTO, Car car) {
        BeanUtils.copyProperties(carDTO, car, "id", "calendar", "carPhotos");

        car.getCalendar().clear();
        if (carDTO.getCalendar() != null) {
            car.getCalendar().addAll(carDTO.getCalendar());

            for (Calendar calendar : car.getCalendar()) {
                calendar.setCar(car);
            }
        }

        car.getCarPhotos().clear();
        if (carDTO.getCarPhotos() != null) {
            for (String carPhoto : carDTO.getCarPhotos()) {
                CarPhoto carPhotoFromDTO = new CarPhoto();

                carPhotoFromDTO.setPhoto(Base64Utils.decodeFromString(carPhoto));
                carPhotoFromDTO.setCar(car);

                car.getCarPhotos().add(carPhotoFromDTO);
            }
        }
    }
}
