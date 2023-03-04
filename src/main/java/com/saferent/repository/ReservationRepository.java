package com.saferent.repository;

import com.saferent.domain.Reservation;
import com.saferent.domain.User;
import com.saferent.domain.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH Car c on r.car=c.id WHERE " +
            "c.id=:carId AND (r.status NOT IN :status) AND :pickUpTime BETWEEN r.pickUpTime and r.dropOfTime " +
            "OR " +
            "c.id=:carId AND (r.status NOT IN :status) AND :dropOfTime BETWEEN r.pickUpTime and r.dropOfTime " +
            "OR " +
            "c.id=:carId AND (r.status NOT IN :status) AND (r.pickUpTime BETWEEN :pickUpTime AND :dropOfTime)")
    List<Reservation> checkCarStatus(@Param("carId") Long carId,
                                     @Param("pickUpTime") LocalDateTime pickUpTime,
                                     @Param("dropOfTime") LocalDateTime dropOfTime,
                                     @Param("status") ReservationStatus[] status);

    @EntityGraph(attributePaths = {"car", "car.image"})
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"car", "car.image"})
    Page<Reservation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.image", "user"})
    Optional<Reservation> findById(Long id);

    @EntityGraph(attributePaths = {"car", "car.image", "user"})
    Page<Reservation> findAllByUser(User user, Pageable pageable);
}


