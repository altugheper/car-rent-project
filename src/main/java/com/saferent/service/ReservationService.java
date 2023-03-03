package com.saferent.service;

import com.saferent.domain.Car;
import com.saferent.domain.Reservation;
import com.saferent.domain.User;
import com.saferent.domain.enums.ReservationStatus;
import com.saferent.dto.request.ReservationRequest;
import com.saferent.exception.BadRequestException;
import com.saferent.exception.message.ErrorMessage;
import com.saferent.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void createReservation(ReservationRequest reservationRequest, User user, Car car) {

        checkReservationTimeIsCorrect(reservationRequest.getPickUpTime(), reservationRequest.getDropOfTime());

        boolean carStatus = checkCarAvailibility(car, reservationRequest.getPickUpTime(),reservationRequest.getDropOfTime());


    }
    //!!! Istenen rezervasyon tarihleri dogru mu?
    private void checkReservationTimeIsCorrect(LocalDateTime pickUpTime,
                                               LocalDateTime dropOfTime){
        LocalDateTime now = LocalDateTime.now();

        if(pickUpTime.isBefore(now)) {
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

        //!!! baş. tarihi ve bitiş tarihi biribirine eşit mi
        boolean isEqual = pickUpTime.isEqual(dropOfTime)?true:false;
        // !!! baş. tarihi, bitiş tarihinin öncesinde mi
        boolean isBefore = pickUpTime.isBefore(dropOfTime)?true:false; // !!!

        if(isEqual || !isBefore){
            throw  new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

    }

    //!!! Reserve edilen araca musait mi?
    private boolean checkCarAvailibility(Car car, LocalDateTime pickUpTime,
                                         LocalDateTime dropOfTime){

        List<Reservation> existReservations = getConflictReservations(car,pickUpTime,dropOfTime);

        return existReservations.isEmpty();

    }

    //!!! Fiyat hesaplamasi
    private Double getTotalPrice(Car car, LocalDateTime pickUpTime,
                                 LocalDateTime dropOfTime){
        Long minutes = ChronoUnit.MINUTES.between(pickUpTime, dropOfTime);
        double hours = Math.ceil(minutes/60/0);
        return car.getPricePerHour() * hours;

    }

    // Rezervasyonlar arasi cakisma var mi?
    private List<Reservation> getConflictReservations(Car car, LocalDateTime pickUpTime, LocalDateTime dropOfTime){
        if (pickUpTime.isAfter(dropOfTime)){
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

        ReservationStatus[] status = {ReservationStatus.CANCELED, ReservationStatus.DONE};

        List<Reservation> existReservation =
                reservationRepository.checkCarStatus(car.getId(),pickUpTime,dropOfTime,status);

        return existReservation;
    }
}
