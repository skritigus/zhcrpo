package com.bootgussy.dancecenterservice.core.config;

import com.bootgussy.dancecenterservice.core.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;

/*@Component
public class JwtUtils {
    private String secret = "dance_studio_very_secret_key_1234567890"; // В идеале брать из application.properties
    final private int jwtExpirationMs = 86400000;

    public String generateToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // Здесь можно логировать ошибки: просрочен, неверная подпись и т.д.
        }
        return false;
    }
}*/
