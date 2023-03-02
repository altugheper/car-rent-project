package com.saferent.repository;

import com.saferent.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    // !!! JPQL
    @Query("SELECT count(*) from Car c join c.image img where img.id=:id")
    Integer findCarCountByImageId(@Param("id") String id);


    @EntityGraph(attributePaths = {"image"})//!!! EAGER yaptik
    List<Car> findAll();

    @EntityGraph(attributePaths = {"image"})//!!! EAGER yaptik
    Page<Car> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"image"})//!!! EAGER yaptik
    Optional<Car> findCarById(Long id);

    @Query("SELECT c FROM Car c JOIN c.image im WHERE im.id=:id")
    List<Car> findCarsByImageId(@Param("id") String id);
}
