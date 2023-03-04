package com.saferent.mapper;

import com.saferent.domain.ImageFile;
import com.saferent.domain.Reservation;
import com.saferent.domain.User;
import com.saferent.dto.ReservationDTO;
import com.saferent.dto.request.ReservationRequest;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation reservationRequestToReservation(ReservationRequest reservationRequest);

    @Mapping(source = "car.image", target = "car.image", qualifiedByName = "getImageAsString")
    @Mapping(source = "user", target = "userId", qualifiedByName = "getUserId")
    ReservationDTO reservationToreservationDTO(Reservation reservation);

    List<ReservationDTO> map(List<Reservation> reservationList);

    @Named("getImageAsString")
    public static Set<String> getImageIds(Set<ImageFile> imageFiles){
        Set<String> imgs = new HashSet<>();

        imgs = imageFiles.stream().map(imFile -> imFile.getId().toString()).
                collect(Collectors.toSet());

        return imgs;
    }

    @Named("getUserById")
    public static Long getUserId(User user){
        return user.getId();
    }



}