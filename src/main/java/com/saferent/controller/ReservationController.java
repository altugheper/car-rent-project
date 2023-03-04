package com.saferent.controller;

import com.saferent.domain.Car;
import com.saferent.domain.User;
import com.saferent.dto.ReservationDTO;
import com.saferent.dto.request.ReservationRequest;
import com.saferent.dto.response.ResponseMessage;
import com.saferent.dto.response.SfResponse;
import com.saferent.service.CarService;
import com.saferent.service.ReservationService;
import com.saferent.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    private final CarService carService;

    private final UserService userService;

    private final ReservationRequest reservationRequest;

    public ReservationController(ReservationService reservationService, CarService carService, UserService userService, ReservationRequest reservationRequest) {
        this.reservationService = reservationService;
        this.carService = carService;
        this.userService = userService;
        this.reservationRequest = reservationRequest;
    }

    // !!! make Reservation
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SfResponse> makeReservation(@RequestParam("carId") Long carId,
                                                      @Valid @RequestBody ReservationRequest reservationRequest) {

        Car car = carService.getCarById(carId);
        User user = userService.getCurrentUser();

        reservationService.createReservation(reservationRequest, user, car);

        SfResponse response =
                new SfResponse(ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    //!!! AdminMakeReservation
    @PostMapping("/add/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> addReservation(
            @RequestParam("userId") Long userId,
            @RequestParam("carId") Long carId,
            @Valid @RequestBody ReservationRequest reservationRequest){

        Car car = carService.getCarById(carId);
        User user = userService.getById(userId);

        reservationService.createReservation(reservationRequest,user,car);
        SfResponse response =
                new SfResponse(ResponseMessage.RESERVATION_CREATED_RESPONSE_MESSAGE, true);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    //!!! GetAllReservation
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(){
        List<ReservationDTO> allReservations = reservationService.getAllReservations();
        return ResponseEntity.ok(allReservations);
    }

    //!!! GetAllReservationWithPage
    @GetMapping("/admin/all/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDTO>> getAllReservationsWithPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String prop, // neye gore siralanacagi belirtiliyor
            @RequestParam(value="direction",
                    required = false, // direction required olmasin
                    defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<ReservationDTO> allReservations = reservationService.getAllWithPage(pageable);

        return ResponseEntity.ok(allReservations);
    }
}
