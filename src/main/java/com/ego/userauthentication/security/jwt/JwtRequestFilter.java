package com.ego.userauthentication.security.jwt;

import com.ego.userauthentication.business.service.AuthService;
import com.ego.userauthentication.business.service.Impl.AuthServiceImpl;
import com.ego.userauthentication.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthServiceImpl authServiceImpl;
    private AuthServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Eğer yol izin verilen bir yolsa kontrol yapmadan devam et
        String requestPath = request.getServletPath();
        if (requestPath.equals("/register") || requestPath.equals("/authenticate")) {
            chain.doFilter(request, response);
            return; // Hiçbir işlem yapma, filtreyi geçir
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Authorization Header yoksa veya Bearer ile başlamıyorsa
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("JWT Token does not begin with Bearer String");
            chain.doFilter(request, response); // Doğrulama yapmadan devam
            return;
        }

        jwt = authorizationHeader.substring(7); // Bearer'ı kesip sadece tokeni al
        username = jwtTokenUtil.extractUsername(jwt);

        // Kullanıcı doğrulama ve token kontrolü burada yapılır
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        chain.doFilter(request, response);
    }
}
