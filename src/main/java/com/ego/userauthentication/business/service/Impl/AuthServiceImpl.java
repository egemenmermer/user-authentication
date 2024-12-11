package com.ego.userauthentication.service.Impl;

import com.ego.userauthentication.data.entity.UserEntity;
import com.ego.userauthentication.data.repository.UserRepository;
import com.ego.userauthentication.dto.LoginRequestDto;
import com.ego.userauthentication.dto.LoginResponseDto;
import com.ego.userauthentication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        UserEntity userEntity = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));;


        if ( !userEntity.getPassword().equals(loginRequestDto.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new LoginResponseDto("Login Successfull");
    }
}
