package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.api.dto.create.LoginRequest;
import com.bootgussy.dancecenterservice.api.dto.response.AuthenticationResponse;
import com.bootgussy.dancecenterservice.core.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
/*import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;*/
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "API for authentification")
public class AuthenticationController {
    //private AuthenticationManager authenticationManager;
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request) {

        authService.register(request, "ROLE_STUDENT");
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        //try {

            /*Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );*/

            //SecurityContextHolder.getContext().setAuthentication(authentication);


            /*List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();*/

            return ResponseEntity.ok(ResponseEntity.badRequest()/*new AuthenticationResponse(loginRequest.getUsername(), roles)*/);

            //} catch (/*BadCredentialsException e*/) {

            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong login or password");
        //}
    }
}
