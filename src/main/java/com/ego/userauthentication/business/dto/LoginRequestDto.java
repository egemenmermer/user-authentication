package com.ego.userauthentication.business.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class LoginRequestDto implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
