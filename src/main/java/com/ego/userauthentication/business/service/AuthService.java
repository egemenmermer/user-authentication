package com.ego.userauthentication.service;

import com.ego.userauthentication.dto.LoginRequestDto;
import com.ego.userauthentication.dto.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequestDto);

}
