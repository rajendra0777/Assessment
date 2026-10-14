package com.caprae.saasquatch.service;

import com.caprae.saasquatch.config.JwtTokenProvider;
import com.caprae.saasquatch.dto.AuthRequest;
import com.caprae.saasquatch.dto.AuthResponse;
import com.caprae.saasquatch.dto.RegisterRequest;
import com.caprae.saasquatch.entity.User;
import com.caprae.saasquatch.exception.DuplicateResourceException;
import com.caprae.saasquatch.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_MS = 15 * 60 * 1000L;

    /** Initial password for the two bootstrap accounts. Set SEED_PASSWORD; if blank a random one is generated and logged once. */
    @Value("${app.seed.password:}")
    private String seedPassword;

    private final Map<String, long[]> failedAttempts = new ConcurrentHashMap<>(); // username -> {count, lastFailureMs}

    @PostConstruct
    public void initDefaultAccounts() {
        boolean needsAdmin = !userRepository.existsByUsername("admin");
        boolean needsSearcher = !userRepository.existsByUsername("searcher");
        if (!needsAdmin && !needsSearcher) {
            return;
        }
        String password = seedPassword;
        if (password == null || password.isBlank()) {
            byte[] bytes = new byte[18];
            new SecureRandom().nextBytes(bytes);
            password = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            log.warn("SEED_PASSWORD not set. Generated one-time password for 'admin' and 'searcher': {}", password);
        }
        if (needsAdmin) {
            userRepository.save(new User(null, "admin", "admin@capraecapital.com", passwordEncoder.encode(password),
                    "Admin (demo account)", "ROLE_ADMIN", newApiKey()));
        }
        if (needsSearcher) {
            userRepository.save(new User(null, "searcher", "dealflow@capraecapital.com", passwordEncoder.encode(password),
                    "Deal Sourcing Associate (demo account)", "ROLE_OPERATOR", newApiKey()));
        }
    }

    private static String newApiKey() {
        return "caprae_live_api_" + UUID.randomUUID().toString().replace("-", "");
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        String key = request.getUsername() == null ? "" : request.getUsername().toLowerCase(Locale.ROOT);
        long[] state = failedAttempts.get(key);
        if (state != null && state[0] >= MAX_FAILED_ATTEMPTS && System.currentTimeMillis() - state[1] < LOCKOUT_MS) {
            throw new BadCredentialsException("Too many failed attempts");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElse(null);
        // Always run a BCrypt comparison so response time does not reveal whether the account exists.
        String hash = user != null ? user.getPassword() : passwordEncoder.encode("dummy-password-for-timing");
        boolean matches = passwordEncoder.matches(request.getPassword(), hash) && user != null;

        if (!matches) {
            failedAttempts.merge(key, new long[]{1, System.currentTimeMillis()},
                    (old, fresh) -> new long[]{old[0] + 1, fresh[1]});
            throw new BadCredentialsException("Invalid username or password");
        }
        failedAttempts.remove(key);

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getFullName(), user.getRole(), user.getApiKey());
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        // Never trust a client-supplied role: self-service accounts are always operators.
        String role = "ROLE_OPERATOR";
        String apiKey = newApiKey();

        User newUser = new User(
                null,
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                role,
                apiKey
        );

        userRepository.save(newUser);

        String token = jwtTokenProvider.generateToken(newUser.getUsername(), newUser.getRole());
        return new AuthResponse(
                token,
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getFullName(),
                newUser.getRole(),
                newUser.getApiKey()
        );
    }
}
