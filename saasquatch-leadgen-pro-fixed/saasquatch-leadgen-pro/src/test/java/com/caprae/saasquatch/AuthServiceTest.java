package com.caprae.saasquatch;

import com.caprae.saasquatch.config.JwtTokenProvider;
import com.caprae.saasquatch.dto.AuthRequest;
import com.caprae.saasquatch.dto.AuthResponse;
import com.caprae.saasquatch.dto.RegisterRequest;
import com.caprae.saasquatch.entity.User;
import com.caprae.saasquatch.repository.UserRepository;
import com.caprae.saasquatch.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private static final String SEED = "TestSeedPass123";

    private UserRepository users;
    private PasswordEncoder encoder;
    private AuthService service;
    private User admin;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new AuthService(users, encoder, new JwtTokenProvider("", 3_600_000L));
        ReflectionTestUtils.setField(service, "seedPassword", SEED);
        admin = new User(1L, "admin", "admin@capraecapital.com", encoder.encode(SEED), "Admin", "ROLE_ADMIN", "key");
        when(users.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(users.findByUsername("ghost")).thenReturn(Optional.empty());
        when(users.findByEmail(any())).thenReturn(Optional.empty());
    }

    private static AuthRequest req(String user, String pass) {
        AuthRequest r = new AuthRequest();
        r.setUsername(user);
        r.setPassword(pass);
        return r;
    }

    @Test
    @DisplayName("Correct password issues a token")
    void loginSucceeds() {
        AuthResponse res = service.login(req("admin", SEED));
        assertNotNull(res.getToken());
        assertEquals("ROLE_ADMIN", res.getRole());
    }

    @Test
    @DisplayName("Bootstrap default password ADMIN is accepted when configured")
    void defaultAdminPasswordAccepted() {
        AuthService defaultService = new AuthService(users, encoder, new JwtTokenProvider("", 3_600_000L));
        ReflectionTestUtils.setField(defaultService, "seedPassword", "ADMIN");
        User testAdmin = new User(2L, "admin", "admin@capraecapital.com", encoder.encode("ADMIN"), "Admin", "ROLE_ADMIN", "key");
        when(users.findByUsername("admin")).thenReturn(Optional.of(testAdmin));
        AuthResponse res = defaultService.login(req("admin", "ADMIN"));
        assertNotNull(res.getToken());
        assertEquals("ROLE_ADMIN", res.getRole());
    }

    @Test
    @DisplayName("Regression: the old hardcoded passwords no longer work")
    void oldBackdoorsAreGone() {
        assertThrows(BadCredentialsException.class, () -> service.login(req("admin", "Caprae2025!")));
        assertThrows(BadCredentialsException.class, () -> service.login(req("admin", "admin")));
    }

    @Test
    @DisplayName("Unknown users get the same error as wrong passwords")
    void unknownUser() {
        assertThrows(BadCredentialsException.class, () -> service.login(req("ghost", SEED)));
    }

    @Test
    @DisplayName("Account locks after repeated failures, even for the right password")
    void lockout() {
        for (int i = 0; i < 5; i++) {
            assertThrows(BadCredentialsException.class, () -> service.login(req("admin", "wrong-password")));
        }
        assertThrows(BadCredentialsException.class, () -> service.login(req("admin", SEED)));
    }

    @Test
    @DisplayName("Registration ignores a client-supplied admin role")
    void registerForcesOperatorRole() {
        when(users.existsByUsername("eve")).thenReturn(false);
        when(users.existsByEmail("eve@example.com")).thenReturn(false);
        RegisterRequest r = new RegisterRequest();
        r.setUsername("eve");
        r.setEmail("eve@example.com");
        r.setPassword("a-long-password-1");
        r.setFullName("Eve");
        r.setRole("ROLE_ADMIN");

        AuthResponse res = service.register(r);

        assertEquals("ROLE_OPERATOR", res.getRole());
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        assertEquals("ROLE_OPERATOR", saved.getValue().getRole());
        assertNotEquals("a-long-password-1", saved.getValue().getPassword()); // stored hashed
    }

    @Test
    @DisplayName("Seed accounts are created with the configured password, hashed")
    void seedsAccounts() {
        when(users.existsByUsername(anyString())).thenReturn(false);
        service.initDefaultAccounts();
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(users, times(2)).save(saved.capture());
        for (User u : saved.getAllValues()) {
            assertTrue(encoder.matches(SEED, u.getPassword()));
            assertNotEquals(SEED, u.getPassword());
        }
    }
}
