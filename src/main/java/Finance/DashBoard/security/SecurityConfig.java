package Finance.DashBoard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// ✅ ADD THESE IMPORTS
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // ✅ EXISTING CODE
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/dashboard/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }

    // 🔥 ADD THIS METHOD BELOW (VERY IMPORTANT)
    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        UserDetails analyst = User.withDefaultPasswordEncoder()
                .username("analyst")
                .password("analyst123")
                .roles("ANALYST")
                .build();

        UserDetails viewer = User.withDefaultPasswordEncoder()
                .username("viewer")
                .password("viewer123")
                .roles("VIEWER")
                .build();

        return new InMemoryUserDetailsManager(admin, analyst, viewer);
    }
}