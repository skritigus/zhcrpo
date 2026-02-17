package com.bootgussy.dancecenterservice.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/*import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;*/
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/*@Component
public class AuthentificationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Получаем роль из заголовка (например: ROLE_ADMIN, ROLE_TRAINER, ROLE_STUDENT)
        String role = request.getHeader("X-Role");

        if (role != null && !role.isEmpty()) {
            // Создаем временного пользователя "DebugUser" с указанной ролью
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "DebugUser", null, Collections.singletonList(new SimpleGrantedAuthority(role))
            );

            // Устанавливаем пользователя в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}*/
