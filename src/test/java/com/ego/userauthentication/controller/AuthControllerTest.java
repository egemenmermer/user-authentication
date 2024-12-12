package com.ego.userauthentication.controller;

import com.ego.userauthentication.business.dto.LoginRequestDto;
import com.ego.userauthentication.business.dto.LoginResponseDto;
import com.ego.userauthentication.business.service.AuthService;
import com.ego.userauthentication.util.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Should return token when valid login credentials are provided")
    void testCreateAuthenticationTokenSuccess() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("validUser");
        loginRequestDto.setPassword("validPass");
        String token = "mockedToken";

        UserDetails mockedUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetailsService.loadUserByUsername("validUser")).thenReturn(mockedUserDetails);
        Mockito.when(jwtTokenUtil.generateToken(mockedUserDetails)).thenReturn(token);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"validUser\",\"password\":\"validPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    @DisplayName("Should return error when invalid credentials are provided")
    void testCreateAuthenticationTokenInvalidCredentials() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("invalidUser");
        loginRequestDto.setPassword("invalidPass");

        Mockito.doThrow(new BadCredentialsException("INVALID_CREDENTIALS"))
                .when(authenticationManager).authenticate(any());

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"invalidUser\",\"password\":\"invalidPass\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return error when user is disabled")
    void testCreateAuthenticationTokenUserDisabled() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("disabledUser");
        loginRequestDto.setPassword("password");

        Mockito.doThrow(new Exception("USER_DISABLED"))
                .when(authenticationManager).authenticate(any());

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"disabledUser\",\"password\":\"password\"}"))
                .andExpect(status().is4xxClientError());
    }
}