package com.wargacare.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "d2FyZ2FjYXJlLWxvY2FsLWRldmVsb3BtZW50LXNlY3JldC1rZXktMjU2Yml0LWxvbmc=");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    @DisplayName("isTokenValid() - Mengembalikan true untuk user aktif dengan token valid")
    void isTokenValid_ActiveUser_ReturnsTrue() {
        UserDetails activeUser = new User("active@wargacare.id", "password", true, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_WARGA")));

        String token = jwtUtil.generateToken(activeUser);

        boolean isValid = jwtUtil.isTokenValid(token, activeUser);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid() - Mengembalikan false jika user dinonaktifkan (isEnabled = false)")
    void isTokenValid_DisabledUser_ReturnsFalse() {
        UserDetails activeUser = new User("disabled@wargacare.id", "password", true, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_WARGA")));

        String token = jwtUtil.generateToken(activeUser);

        UserDetails disabledUser = new User("disabled@wargacare.id", "password", false, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_WARGA")));

        boolean isValid = jwtUtil.isTokenValid(token, disabledUser);
        assertThat(isValid).isFalse();
    }
}
