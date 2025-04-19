package dev.antonis.your_digital_bridge.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.antonis.your_digital_bridge.entity.UserCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private Integer id;
    private String username;
    @JsonIgnore
    private String password;

    public UserDetailsImpl(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static UserDetailsImpl build(UserCredential userCredential) {
        return new UserDetailsImpl(
                userCredential.getUser().getId(),
                userCredential.getUsername(),
                userCredential.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account is always considered non-expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account is always considered non-locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials are always considered non-expired
    }

    @Override
    public boolean isEnabled() {
        return true; // User is always considered enabled
    }
}
