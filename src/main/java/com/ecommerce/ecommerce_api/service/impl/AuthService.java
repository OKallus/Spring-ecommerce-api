package com.ecommerce.ecommerce_api.service.impl;

import com.ecommerce.ecommerce_api.dto.request.LoginRequest;
import com.ecommerce.ecommerce_api.dto.request.RegisterRequest;
import com.ecommerce.ecommerce_api.dto.response.AuthResponse;
import com.ecommerce.ecommerce_api.entity.Cart;
import com.ecommerce.ecommerce_api.entity.User;
import com.ecommerce.ecommerce_api.repository.CartRepository;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import com.ecommerce.ecommerce_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        // Cria carrinho vazio para o novo usuário
        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
