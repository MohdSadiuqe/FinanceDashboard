package Finance.DashBoard.config;

import Finance.DashBoard.model.FinancialRecord;
import Finance.DashBoard.model.RecordType;
import Finance.DashBoard.model.Role;
import Finance.DashBoard.model.User;
import Finance.DashBoard.repository.FinancialRecordRepository;
import Finance.DashBoard.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDemoData(UserRepository userRepository, FinancialRecordRepository recordRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(user("Admin User", "admin@demo.local", "Admin123!", Role.ADMIN, true, passwordEncoder));
                userRepository.save(user("Analyst User", "analyst@demo.local", "Analyst123!", Role.ANALYST, true, passwordEncoder));
                userRepository.save(user("Viewer User", "viewer@demo.local", "Viewer123!", Role.VIEWER, true, passwordEncoder));
            }

            if (recordRepository.count() == 0) {
                User admin = userRepository.findByEmail("admin@demo.local").orElseThrow();
                LocalDate today = LocalDate.now();
                recordRepository.saveAll(List.of(
                        financial(admin, 5200, RecordType.INCOME, "Salary", today.withDayOfMonth(1), "Monthly salary"),
                        financial(admin, 800, RecordType.INCOME, "Freelance", today.minusDays(5), "Side project"),
                        financial(admin, 1200, RecordType.EXPENSE, "Rent", today.withDayOfMonth(2), "Apartment"),
                        financial(admin, 350, RecordType.EXPENSE, "Groceries", today.minusDays(1), "Weekly shop"),
                        financial(admin, 120, RecordType.EXPENSE, "Transport", today.minusDays(3), "Transit pass")
                ));
            }
        };
    }

    private static User user(String name, String email, String rawPassword, Role role, boolean active,
                             PasswordEncoder passwordEncoder) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setIsActive(active);
        return u;
    }

    private static FinancialRecord financial(User creator, double amount, RecordType type, String category,
                                             LocalDate date, String note) {
        FinancialRecord r = new FinancialRecord();
        r.setAmount(amount);
        r.setType(type);
        r.setCategory(category);
        r.setDate(date);
        r.setNote(note);
        r.setCreatedBy(creator);
        return r;
    }
}
