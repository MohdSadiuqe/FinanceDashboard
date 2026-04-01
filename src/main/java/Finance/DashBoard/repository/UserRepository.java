package Finance.DashBoard.repository;

import Finance.DashBoard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}