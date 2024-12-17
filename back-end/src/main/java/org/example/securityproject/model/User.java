package org.example.securityproject.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;

import org.example.securityproject.enums.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;


@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "firstName", nullable = true)
    private String firstName;

    @Column(name = "lastName", nullable = true)
    private String lastName;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "phoneNumber", nullable = true)
    private String phoneNumber;

    @Column(name = "userType", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "userRoles", joinColumns = @JoinColumn(name = "userId"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles;

    @Column(name = "packageService", nullable = false)
    @Enumerated(EnumType.STRING)
    private PackageService packageService;

    @Column(name = "registrationStatus", nullable = false)
    private RegistrationStatus registrationStatus;

    //za pravno lice
    @Column(name = "companyName", nullable = true)
    private String companyName;

    @Column(name = "pib", nullable = true)
    private String pib;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User() {}

    public User(Integer id, String email, String password, String salt, String firstName, String lastName, String address, String city, String country, String phoneNumber, UserType userType, List<UserRole> roles, PackageService packageServce, RegistrationStatus registrationStatus, String companyName, String pib) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.packageService = packageServce;
        this.roles = roles;
        this.registrationStatus = registrationStatus;
        this.companyName = companyName;
        this.pib = pib;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

        @Override
    public String getUsername() {
        return this.email; // Ova metoda je obavezna zbog UserDetails interfejsa
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password); // hashing lozinke
    }

    public boolean checkPassword(String password) {
        return passwordEncoder.matches(password, this.password);  // provera da li lozinka odgovara
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if(userType == UserType.INDIVIDUAL) {
            this.firstName = firstName;
        }
        else {
            this.firstName = null;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(userType == UserType.INDIVIDUAL) {
            this.lastName = lastName;
        }
        else {
            this.lastName = null;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public PackageService getPackageService() {
        return packageService;
    }

    public void setPackageService(PackageService packageService) {
        this.packageService = packageService;
    }
    
    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        if(userType == UserType.LEGAL) {
            this.companyName = companyName;
        }
        else {
            this.companyName = null;
        }
    }

    public String getPib() {
        return pib;
    }

    public void setPib(String pib) {
        if(userType == UserType.LEGAL) {
            this.pib = pib;
        }
        else {
            this.pib = null;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (roles != null) {
            for (UserRole role : roles) {
                authorities.addAll(role.getAuthorities());
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; 
    }
}
