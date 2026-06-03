package com.wargacare.auth;

import com.wargacare.auth.dto.LoginRequest;
import com.wargacare.auth.dto.RegisterRequest;
import com.wargacare.security.JwtUtil;
import com.wargacare.user.User;
import com.wargacare.user.UserRepository;
import com.wargacare.user.UserRole;
import com.wargacare.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Budi Santoso")
                .email("budi@example.com")
                .password("hashed_password")
                .role(UserRole.WARGA)
                .rt("001")
                .rw("005")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest(
                "Budi Santoso",
                "budi@example.com",
                "password123",
                "001",
                "005",
                "08123456789",
                "Jl. Merdeka No. 1"
        );

        loginRequest = new LoginRequest("budi@example.com", "password123");
    }

    @Test
    @DisplayName("register() - Berhasil mendaftarkan pengguna baru")
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt_token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        var result = authService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt_token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getUser().getEmail()).isEqualTo("budi@example.com");
        assertThat(result.getUser().getRole()).isEqualTo(UserRole.WARGA);

        verify(userRepository).existsByEmail("budi@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() - Gagal jika email sudah terdaftar")
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("budi@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah terdaftar");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login() - Berhasil login dengan kredensial valid")
    void login_Success() {
        UserDetails mockUserDetails = new org.springframework.security.core.userdetails.User(
                "budi@example.com",
                "hashed_password",
                List.of(new SimpleGrantedAuthority("ROLE_WARGA"))
        );
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities()));
        when(userRepository.findByEmail("budi@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt_token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);

        var result = authService.login(loginRequest);

        assertThat(result.getAccessToken()).isEqualTo("jwt_token");
        assertThat(result.getUser().getEmail()).isEqualTo("budi@example.com");
    }

    @Test
    @DisplayName("login() - Gagal dengan password salah")
    void login_WrongPassword_ThrowsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("getCurrentUser() - Berhasil mendapatkan data user")
    void getCurrentUser_Success() {
        when(userRepository.findByEmail("budi@example.com")).thenReturn(Optional.of(testUser));

        UserResponse result = authService.getCurrentUser("budi@example.com");

        assertThat(result.getEmail()).isEqualTo("budi@example.com");
        assertThat(result.getFullName()).isEqualTo("Budi Santoso");
        assertThat(result.getRole()).isEqualTo(UserRole.WARGA);
    }
}
