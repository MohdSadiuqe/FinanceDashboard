package Finance.DashBoard.controller;

import Finance.DashBoard.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {

        String username = req.get("username");

        String role = "ADMIN";

        String token = jwtUtil.generateToken(username, role);

        return Map.of("token", token);
    }
}