package org.example.securityproject.service;

import org.example.securityproject.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;

import org.example.securityproject.dto.RegistrationResponseDto;
import org.example.securityproject.dto.UserDto;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.enums.UserType;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public RegistrationResponseDto registerUser(@Valid UserDto userDto) {
        logger.info("Starting user registration for email: {}", userDto.getEmail());
        RegistrationResponseDto response = new RegistrationResponseDto();
        
        User existingUser = userRepository.findByEmail(userDto.getEmail());
        if(existingUser != null) {
            response.setMessageResponse("User with this email already exists");
            response.setFlag(false);
            return response;
        }
        
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); //cuvam hashovanu sifru
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAddress(userDto.getAddress());
        user.setCity(userDto.getCity());
        user.setCountry(userDto.getCountry());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRoles(userDto.getRoles());
        user.setUserType(userDto.getUserType());
        user.setRegistrationStatus(RegistrationStatus.PENDING);
        user.setPackageService(userDto.getPackageService());
        user.setCompanyName(userDto.getCompanyName());
        user.setPib(userDto.getPib());
        
        try {
            userRepository.save(user);
        } catch (DataAccessException e) {
            logger.error("Error saving user to the database: {}", e.getMessage());
            response.setFlag(false);
            response.setMessageResponse("Error during registration. Please try again later.");
            return response;
        }

        userRepository.save(user);

        response.setFlag(true);
        response.setMessageResponse("Registration successfull");
        return response;
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
