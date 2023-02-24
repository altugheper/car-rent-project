package com.saferent.repository;

import com.saferent.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository // optional
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);



    @EntityGraph(attributePaths = "roles") // Defaultta Lazy olan Role bilgilerini EAGER yaptık
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "roles") // 2 query
    List<User> findAll(); // 4 query

    @EntityGraph(attributePaths = "roles") // 2 query
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(Long id);
}
