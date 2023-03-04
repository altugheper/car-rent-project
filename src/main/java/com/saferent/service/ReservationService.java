package com.saferent.service;

import com.saferent.domain.Car;
import com.saferent.domain.Reservation;
import com.saferent.domain.User;
import com.saferent.domain.enums.ReservationStatus;
import com.saferent.dto.ReservationDTO;
import com.saferent.dto.request.ReservationRequest;
import com.saferent.dto.request.ReservationUpdateRequest;
import com.saferent.exception.BadRequestException;
import com.saferent.exception.ResourceNotFoundException;
import com.saferent.exception.message.ErrorMessage;
import com.saferent.mapper.ReservationMapper;
import com.saferent.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    public void createReservation(ReservationRequest reservationRequest, User user, Car car) {

        checkReservationTimeIsCorrect(reservationRequest.getPickUpTime(), reservationRequest.getDropOfTime());

        boolean carStatus = checkCarAvailability(car , reservationRequest.getPickUpTime(), reservationRequest.getDropOfTime());

        Reservation reservation = reservationMapper.reservationRequestToReservation(reservationRequest);

        if(carStatus){
            reservation.setStatus(ReservationStatus.CREATED);
        } else {
            throw new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE);
        }
        reservation.setCar(car);
        reservation.setUser(user);

        Double totalPrice = getTotalPrice(car, reservationRequest.getPickUpTime(),reservationRequest.getDropOfTime());

        reservation.setTotalPrice(totalPrice);

        reservationRepository.save(reservation);

    }
    // !!! Istenen rezervasyon tarihleri doğru mu ???
    public void checkReservationTimeIsCorrect(LocalDateTime pickUpTime,
                                               LocalDateTime dropOfTime){
        LocalDateTime now = LocalDateTime.now();

        if(pickUpTime.isBefore(now)) {
            throw new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

        //!!! baç. tarihi ve bitiş tarihi biribirine eşit mi
        boolean isEqual = pickUpTime.isEqual(dropOfTime)?true:false;
        // !!! baş. tarihi, bitiş tarihinin öncesinde mi
        boolean isBefore = pickUpTime.isBefore(dropOfTime)?true:false; // !!!

        if(isEqual || !isBefore){
            throw  new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

    }

    // !!! Araç müsait mi ???
    public boolean checkCarAvailability(Car car,LocalDateTime pickUpTime,
                                         LocalDateTime dropOfTime) {

        List<Reservation> existReservations = getConflictReservations(car,pickUpTime,dropOfTime);

        return existReservations.isEmpty();

    }

    // !!! Fiyat Hesaplaması
    public Double getTotalPrice(Car car,LocalDateTime pickUpTime,
                                 LocalDateTime dropOfTime){
        Long minutes =  ChronoUnit.MINUTES.between(pickUpTime,dropOfTime);
        double hours = Math.ceil(minutes/60.0);
        return car.getPricePerHour() * hours;

    }

    // !!! Reservasyonlar arası çakışma var mı ???
    public List<Reservation> getConflictReservations(Car car,LocalDateTime pickUpTime,
                                                      LocalDateTime dropOfTime ){
        if(pickUpTime.isAfter(dropOfTime)){
            throw  new BadRequestException(ErrorMessage.RESERVATION_TIME_INCORRECT_MESSAGE);
        }

        ReservationStatus[] status = {ReservationStatus.CANCELED,ReservationStatus.DONE};

        List<Reservation> existReservation =
                reservationRepository.checkCarStatus(car.getId(),pickUpTime,dropOfTime,status);

        return existReservation;
    }

    public List<ReservationDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservationMapper.map(reservations);
    }

    public Page<ReservationDTO> getAllWithPage(Pageable pageable) {
        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
        return reservationPage.map(reservationMapper::reservationToreservationDTO);
    }

    public void updateReservation(Long reservationId, Car car, ReservationUpdateRequest reservationUpdateRequest) {
        Reservation reservation = getById(reservationId);
        //!!! rezervasyon status "cancel" vbeya "done" ise update islemi yapilmasin
        if (reservation.getStatus().equals(ReservationStatus.CANCELED) ||
                reservation.getStatus().equals(ReservationStatus.DONE)) {
            throw new BadRequestException(ErrorMessage.RESERVATION_STATUS_CANT_CHANGE_MESSAGE);
        }
        //!!! create update edilecekken statusu create yapilmayacaksa pickUpTime ve
        // dropOfTime kontrolu yapilmasin
        if (reservationUpdateRequest.getStatus() != null &&
                reservationUpdateRequest.getStatus()==ReservationStatus.CREATED){
            checkReservationTimeIsCorrect(reservationUpdateRequest.getPickUpTime(),
                    reservationUpdateRequest.getDropOfTime());
            //!!! Conflict kontrolu
            List<Reservation> conflictReservations = getConflictReservations(car,
                    reservationUpdateRequest.getPickUpTime(),
                    reservationUpdateRequest.getDropOfTime());
            if (!conflictReservations.isEmpty()) {
                if (!(conflictReservations.size()==1 &&
                        conflictReservations.get(0).getId().equals(reservationId))){
                    throw new BadRequestException(ErrorMessage.CAR_NOT_AVAILABLE_MESSAGE);
                }
            }
            //!!! fiyat hesaplamasi
            Double totalPrice = getTotalPrice(car, reservationUpdateRequest.getPickUpTime(),reservationUpdateRequest.getDropOfTime());

            reservation.setTotalPrice(totalPrice);
            reservation.setCar(car);
        }
        reservation.setPickUpTime(reservationUpdateRequest.getPickUpTime());
        reservation.setDropOfTime(reservationUpdateRequest.getDropOfTime());
        reservation.setDropOfLocation(reservationUpdateRequest.getDropOfLocation());
        reservation.setPickUpLocation(reservationUpdateRequest.getPickUpLocation());
        reservation.setStatus(reservationUpdateRequest.getStatus());

        reservationRepository.save(reservation);
    }

    public Reservation getById(Long id){
        Reservation reservation = reservationRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION, id)));

        return reservation;
    }

    public ReservationDTO getReservationDTO(Long id) {
        Reservation reservation = getById(id);
        return reservationMapper.reservationToreservationDTO(reservation);
    }

    public Page<ReservationDTO> findReservationPageByUser(User user, Pageable pageable) {

        Page<Reservation> reservationPage = reservationRepository.findAllByUser(user,pageable);

        return reservationPage.map(reservationMapper::reservationToreservationDTO);
    }
}
