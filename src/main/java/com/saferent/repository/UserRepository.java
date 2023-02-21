package com.saferent.repository;

import com.saferent.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // optional
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);



    @EntityGraph(attributePaths = "roles") // Defaultta Lazy olan Role bilgilerini EAGER yaptık
    Optional<User> findByEmail(String email);

}
