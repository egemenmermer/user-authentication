package com.ego.userauthentication.business.service;

import com.ego.userauthentication.business.dto.LoginRequestDto;
import com.ego.userauthentication.business.dto.LoginResponseDto;
import com.ego.userauthentication.business.dto.RegisterDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void register(RegisterDto registerDto);


}
