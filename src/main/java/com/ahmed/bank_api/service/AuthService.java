package com.ahmed.bank_api.service;

import com.ahmed.bank_api.dto.auth.LoginRequest;
import com.ahmed.bank_api.dto.auth.LoginResponse;
import com.ahmed.bank_api.dto.auth.RegisterRequest;
import com.ahmed.bank_api.exception.AccountNotFound;
import com.ahmed.bank_api.exception.InvalidCredentialsException;
import com.ahmed.bank_api.exception.UsernameAlreadyExistsException;
import com.ahmed.bank_api.model.User;
import com.ahmed.bank_api.repository.UserRepository;
import com.ahmed.bank_api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository , PasswordEncoder passwordEncoder , JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        return userRepository.save(user);

    }

    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()->new InvalidCredentialsException("Invalid Username or Password"));

        if (!user.isEnabled()){
            throw new InvalidCredentialsException("Account Disabled");
        }

       if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
           throw new InvalidCredentialsException("Invalid Username or Password");
       }

       String token = jwtService.generateToken(user);
       return new  LoginResponse(token);

    }







}
