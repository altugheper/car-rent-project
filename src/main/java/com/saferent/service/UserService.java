package com.saferent.service;

import com.saferent.domain.Role;
import com.saferent.domain.User;
import com.saferent.domain.enums.RoleType;
import com.saferent.dto.request.RegisterRequest;
import com.saferent.exception.ConflictException;
import com.saferent.exception.ResourceNotFoundException;
import com.saferent.exception.message.ErrorMessage;
import com.saferent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {


    private final UserRepository userRepository;


    private final RoleService roleService;


    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_EXCEPTION,email)));
        return user;
    }

    public void saveUser(RegisterRequest registerRequest){
        //!!! DTO'dan gelen email sistemde daha once var mi?
        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ConflictException(
                    String.format(ErrorMessage.EMAIL_ALREADY_EXISTS_MESSAGE,
                            registerRequest.getEmail())
            );
        }

        //!!! Yeni user'in rol bilgisi default olarak customer atiyorum
        Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        //!!! DB'ye gitmedne once sifre encode edilecek
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        //!!! Yeni User'dan gerekli bilgilerini setleyip DB'ye gonderiyorum
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setZipCode(registerRequest.getZipCode());
        user.setRoles(roles);
    }

}
