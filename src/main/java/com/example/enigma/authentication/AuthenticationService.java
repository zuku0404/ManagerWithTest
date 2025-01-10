package com.example.enigma.authentication;

import com.example.enigma.configuration.JwtService;
import com.example.enigma.exception.ErrorMessage;
import com.example.enigma.model.Role;
import com.example.enigma.model.entity.User;
import com.example.enigma.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<Object> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorMessage.LOGIN_EXIST);
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthenticateResponse.builder()
                        .token(jwtToken)
                        .build());
    }

    public ResponseEntity<Object> authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        ));

        Optional<User> userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format(ErrorMessage.USER_NOT_FOUND_BY_EMAIL, request.email()));
        }

        User user = userOptional.get();
        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticateResponse.builder()
                .token(jwtToken)
                .build());
    }
}
