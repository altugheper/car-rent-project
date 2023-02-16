package com.saferent.service;

import com.saferent.domain.ContactMessage;
import com.saferent.exception.ResourceNotFoundException;
import com.saferent.repository.ContactMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public void saveMessage(ContactMessage contactMessage) {
        contactMessageRepository.save(contactMessage);
    }


    public List<ContactMessage> getAll() {
        return contactMessageRepository.findAll();
    }

    public Page<ContactMessage> getAll(Pageable pageable){
        return contactMessageRepository.findAll(pageable);
    }

    public ContactMessage getContactMessage(Long id) {
        return contactMessageRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Contact Message is not found with id"));
    }
}
