package com.saferent.repository;

import com.saferent.domain.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // optional
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}
