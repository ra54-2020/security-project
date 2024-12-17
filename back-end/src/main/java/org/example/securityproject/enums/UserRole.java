package org.example.securityproject.enums;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import static org.example.securityproject.enums.Permission.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
public enum UserRole {
    ADMINISTRATOR(new HashSet<>(Set.of(
        ADMIN_READ,
        ADMIN_CREATE,
        ADMIN_UPDATE,
        ADMIN_DELETE,
        ADMIN_SEEPROFILE,
        CHANGE_PASSWORD,
        CLIENT_READ,
        CLIENT_CREATE,
        CLIENT_UPDATE,
        CLIENT_DELETE,
        EMPLOYEE_READ,
        EMPLOYEE_CREATE,
        EMPLOYEE_UPDATE,
        EMPLOYEE_DELETE
    ))),
    CLIENT(new HashSet<>(Set.of(
        CLIENT_READ,
        CLIENT_CREATE,
        CLIENT_UPDATE,
        CLIENT_DELETE
    ))),
    EMPLOYEE(new HashSet<>(Set.of(
        EMPLOYEE_READ,
        EMPLOYEE_CREATE,
        EMPLOYEE_UPDATE,
        EMPLOYEE_DELETE,
        CHANGE_PASSWORD
    )));

    private Set<Permission> permissions = new HashSet<>();

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
    
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
    
}
