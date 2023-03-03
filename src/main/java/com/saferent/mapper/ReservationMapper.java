package com.saferent.mapper;

import com.saferent.domain.Reservation;
import com.saferent.dto.request.ReservationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation reservationRequestToReservation(ReservationRequest reservationRequest);

}
