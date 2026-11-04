package com.mo5ul.ticketforge.auth;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mo5ul.ticketforge.security.JwtService;
import com.mo5ul.ticketforge.user.User;
import com.mo5ul.ticketforge.user.UserRepository;
@Service 
public class AuthService{
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    public AuthService(UserRepository users,PasswordEncoder encoder, JwtService jwt){
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }
    @Transactional 
    public AuthResponse register(RegisterRequest req){
        String email = req.email().trim().toLowerCase();
        if(users.existsByEmail(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User saved=users.save(new User(email, encoder.encode(req.password())));
        return new AuthResponse(jwt.generateToken(saved));
    }
    public AuthResponse login(LoginRequest req){
        User user =users.findByEmail(req.email().trim().toLowerCase()).filter(u -> encoder.matches(req.password(), u.getPasswordHash())).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials!"));
        return new AuthResponse(jwt.generateToken(user));
    }
}