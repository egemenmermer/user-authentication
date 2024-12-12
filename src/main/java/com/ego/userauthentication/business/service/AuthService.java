package com.ego.userauthentication.business.service;

import com.ego.userauthentication.business.dto.LoginRequestDto;
import com.ego.userauthentication.business.dto.LoginResponseDto;
import com.ego.userauthentication.business.dto.RegisterDto;
import com.ego.userauthentication.data.entity.UserEntity;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequestDto);
    UserEntity register(RegisterDto registerDto);


}
