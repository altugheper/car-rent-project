package com.saferent.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.control.MappingControl;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "t_reservation")
public class Reservation {

    private Long id;

    private Car car;

    private User user;

    private LocalDateTime pickUpTime;

    private LocalDateTime dropOfTime;


}
