package com.carsharing.service;

import com.carsharing.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.Join;

import com.carsharing.model.Car;

import java.util.Calendar;
import java.util.Date;

@Service
public class SearchCarsService {

    @Autowired
    CarRepository carRepository;

    private Specification<Car> init() {
        return (root, query, builder) ->
             builder.isTrue(root.get("activated"));
    }

    private Specification<Car> likeString(String field, String value) {
        return (root, query, builder) ->
                builder.like(root.get(field), value.isEmpty()? "%" : value + "%");
    }

    private Specification<Car> equalShort(String field, Short value) {
        return (root, query, builder) ->
                builder.equal(root.get(field), value);
    }

    private Specification<Car> betweenInteger(String field, Integer lowValue, Integer highValue) {
        return (root, query, builder) ->
                builder.between(root.get(field), lowValue, highValue);
    }

    private Specification<Car> greaterInteger(String field, Integer value) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get(field), value);
    }

    private Specification<Car> lessInteger(String field, Integer value) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get(field), value);
    }

    private Specification<Car> betweenShort(String field, Short lowValue, Short highValue) {
        return (root, query, builder) ->
                builder.between(root.get(field), lowValue, highValue);
    }

    private Specification<Car> greaterShort(String field, Short value) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get(field), value);
    }

    private Specification<Car> lessShort(String field, Short value) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get(field), value);
    }

    private Specification<Car> betweenDouble(String field, Double lowValue, Double highValue) {
        return (root, query, builder) ->
                builder.between(root.get(field), lowValue, highValue);
    }

    private Specification<Car> greaterDouble(String field, Double value) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get(field), value);
    }

    private Specification<Car> lessDouble(String field, Double value) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get(field), value);
    }

    private Specification<Car> rentCondition(Date beginDate, Date endDate) {
        return (root, query, builder) -> {
            Join<Car, Calendar> calendar = root.join("calendar");

            query.distinct(true);

            return builder.and(builder.lessThanOrEqualTo(calendar.get("beginDate"), beginDate),
                               builder.greaterThanOrEqualTo(
                                       builder.function("ADDDATE", Date.class, calendar.get("beginDate"), calendar.get("countDays")), endDate));
        };
    }

    public Page<Car> find(Pageable pageRequest, String mark, String model, Short lowYearIssued, Short highYearIssued,
                          Integer lowMileage, Integer highMileage, Short seatsNumber, String gearboxType, String bodyType,
                          String drive, String engineType, String fuelType, Double lowPrice, Double highPrice,
                          Date rentBeginDate, Short rentCountDays) {
        Specification<Car> spec = init();

        if (mark != null)
            spec = spec.and(likeString("mark", mark));

        if (model != null)
            spec = spec.and(likeString("model", model));

        if (lowYearIssued != null && highYearIssued != null) {
            spec = spec.and(betweenShort("yearIssued", lowYearIssued, highYearIssued));
        } else if (lowYearIssued != null) {
            spec = spec.and(greaterShort("yearIssued", lowYearIssued));
        } else if (highYearIssued != null) {
            spec = spec.and(lessShort("yearIssued", highYearIssued));
        }

        if (lowMileage != null && highMileage != null) {
            spec = spec.and(betweenInteger("mileage", lowMileage, highMileage));
        } else if (lowMileage != null) {
            spec = spec.and(greaterInteger("mileage", lowMileage));
        } else if (highMileage != null) {
            spec = spec.and(lessInteger("mileage", highMileage));
        }

        if (seatsNumber != null)
            spec = spec.and(equalShort("seatsNumber", seatsNumber));

        if (gearboxType != null)
            spec = spec.and(likeString("gearboxType", gearboxType));

        if (bodyType != null)
            spec = spec.and(likeString("bodyType", bodyType));

        if (drive != null)
            spec = spec.and(likeString("drive", drive));

        if (engineType != null)
            spec = spec.and(likeString("engineType", engineType));

        if (fuelType != null)
            spec = spec.and(likeString("fuelType", fuelType));

        if (lowPrice != null && highPrice != null) {
            spec = spec.and(betweenDouble("price", lowPrice, highPrice));
        } else if (lowPrice != null) {
            spec = spec.and(greaterDouble("price", lowPrice));
        } else if (highPrice != null) {
            spec = spec.and(lessDouble("price", highPrice));
        }

        if (rentBeginDate != null && rentCountDays != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(rentBeginDate);
            cal.add(Calendar.DATE, rentCountDays);

            spec = spec.and(rentCondition(rentBeginDate, cal.getTime()));
        }

        return carRepository.findAll(spec, pageRequest);
    }
}
