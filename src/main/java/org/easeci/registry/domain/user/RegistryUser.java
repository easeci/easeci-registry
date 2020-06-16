package org.easeci.registry.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@Entity
@Builder
@Table(name = "registry_user")
@NoArgsConstructor
@AllArgsConstructor
public class RegistryUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String company;
    private String password;
    private Date registrationDate;
    private String website;
    private UserType userType;

    @Column(columnDefinition = "boolean default false")
    private boolean accountNotExpired;

    @Column(columnDefinition = "boolean default false")
    private boolean accountNonLocked;

    @Column(columnDefinition = "boolean default false")
    private boolean credentialsNonExpired;

    @Column(columnDefinition = "boolean default false")
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNotExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
