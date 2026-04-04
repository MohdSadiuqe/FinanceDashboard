package Finance.DashBoard.service;

import Finance.DashBoard.dto.AuthRequest;
import Finance.DashBoard.dto.AuthResponse;
import Finance.DashBoard.exception.BusinessException;
import Finance.DashBoard.model.User;
import Finance.DashBoard.repository.UserRepository;
import Finance.DashBoard.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String roleName = user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), roleName);
        return new AuthResponse(token, user.getEmail(), user.getRole(), expirationMs / 1000);
    }
}
