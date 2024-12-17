package org.example.securityproject.controller;

import org.example.securityproject.dto.*;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/registerUser")
    public ResponseEntity<RegistrationResponseDto> registerUser(@RequestBody UserDto userDto) throws Exception {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.OK);
    }
    
}
