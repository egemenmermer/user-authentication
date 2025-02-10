package com.ego.userauthentication.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.misc.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class RegisterDto {


    private String username;
    private String password;

    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }


}
