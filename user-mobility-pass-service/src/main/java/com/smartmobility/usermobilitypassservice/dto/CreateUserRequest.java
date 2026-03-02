package com.smartmobility.usermobilitypassservice.dto;

import com.smartmobility.usermobilitypassservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
    private String password;
}