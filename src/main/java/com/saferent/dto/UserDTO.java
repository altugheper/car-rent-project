package com.saferent.dto;

import com.saferent.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    // bu class repo'dan gelen pojo'yu DTO'ya cevirmek icin kullanilacak
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String zipCode;
    private Boolean builtIn ;

    private Set<String> roles;

    public void setRoles(Set<Role> roles) {
        Set<String> roleStr = new HashSet<>();
        roles.forEach(r -> {
            roleStr.add(r.getType().getName()); // Customer , Administrator
        });

        this.roles = roleStr;
    }
}
