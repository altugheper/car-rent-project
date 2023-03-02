package com.saferent.dto;

import com.saferent.domain.Car;
import com.saferent.domain.User;
import com.saferent.domain.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {


    private Long id;


    private CarDTO car;


    private Long userId;


    private LocalDateTime pickUpTime;


    private LocalDateTime dropOfTime;


    private String pickUpLocation;


    private String dropOfLocation;


    private ReservationStatus status;


    private Double totalPrice;

}
