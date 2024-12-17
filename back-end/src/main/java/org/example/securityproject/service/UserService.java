package org.example.securityproject.service;

import org.example.securityproject.model.*;
import org.example.securityproject.dto.RegistrationResponseDto;
import org.example.securityproject.dto.UserDto;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.enums.UserType;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResponseDto registerUser(UserDto userDto) {
        validateUserDto(userDto);

        String hashedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(hashedPassword); //cuvam hashovanu sifru
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

        if(userDto.getUserType() == UserType.LEGAL) {
            user.setCompanyName(userDto.getCompanyName());
            user.setPib(userDto.getPib());
        }

        userRepository.save(user);
        RegistrationResponseDto dto = new RegistrationResponseDto();
        dto.setFlag(true);
        dto.setMessageResponse("Registration successfull");
        return dto;
    }

    private void validateUserDto(UserDto userDto) {
        if(userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if(!isEmailValid(userDto.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if(!isPasswordValid(userDto.getPassword())) {
            throw new IllegalArgumentException("Password does not meet security requirements");
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match. Please re-enter both fields");
        } 
        if(!areAllFieldsFilled(userDto)) {
            throw new IllegalArgumentException("all fields are required");
        }
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }    

    private boolean isPasswordValid(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";
        return password != null && password.matches(passwordRegex);
    }

    private boolean areAllFieldsFilled(UserDto userDto) {
        return  userDto.getFirstName() != null && !userDto.getFirstName().isEmpty() &&
                userDto.getLastName() != null && !userDto.getLastName().isEmpty() &&
                userDto.getEmail() != null && !userDto.getEmail().isEmpty() &&
                userDto.getAddress() != null && !userDto.getAddress().isEmpty() &&
                userDto.getCity() != null && !userDto.getCity().isEmpty() &&
                userDto.getCountry() != null && !userDto.getCountry().isEmpty() &&
                userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().isEmpty() &&
                userDto.getUserType() != null &&
                userDto.getRoles() != null &&
                userDto.getPackageService() != null;
    }
}
