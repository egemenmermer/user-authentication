package com.ego.userauthentication.business.service.Impl;

import com.ego.userauthentication.business.dto.RegisterDto;
import com.ego.userauthentication.data.entity.UserEntity;
import com.ego.userauthentication.data.repository.UserRepository;
import com.ego.userauthentication.business.dto.LoginRequestDto;
import com.ego.userauthentication.business.dto.LoginResponseDto;
import com.ego.userauthentication.business.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        UserEntity userEntity = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user found with this username"));;


        if ( !userEntity.getPassword().equals(loginRequestDto.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new LoginResponseDto("Login Successfull");
    }

    @Override
    public void register(RegisterDto registerDto){
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()){
            throw new RuntimeException("Email already exists!");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerDto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(userEntity);

    }
}
