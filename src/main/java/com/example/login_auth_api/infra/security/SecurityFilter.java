package com.example.login_auth_api.infra.security;

import com.example.login_auth_api.domain.user.User;
import com.example.login_auth_api.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         var token = this.recoverToken(request); // Pegando o token do usuário
         var login = tokenService.validateToken(token); // Validar se o token está correto

         if(login != null) {
             User user = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("User not found"));
             var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
             var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
             SecurityContextHolder.getContext().setAuthentication(authentication); // Adicionando o usuário logado no contexto do springSecurity
         }
         filterChain.doFilter(request, response); // se não tiver logado, não Adicionará  no contexto
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
