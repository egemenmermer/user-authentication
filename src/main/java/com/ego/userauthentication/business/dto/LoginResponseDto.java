package com.ego.userauthentication.business.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
public class LoginResponseDto implements Serializable{

    private static final long serialVersionUID = 5926468583005150707L;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
