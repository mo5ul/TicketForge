package com.mo5ul.ticketforge.security;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mo5ul.ticketforge.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService{
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }
     public String generateToken(User user){
        Date now = new Date();
        return Jwts.builder().subject(user.getEmail()).claim("role", user.getRole().name()).issuedAt(now).expiration(new Date(now.getTime() + expirationMs)).signWith(key).compact();
     }
     public Claims parse(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
     }
}