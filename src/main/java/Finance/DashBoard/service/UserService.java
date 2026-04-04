package Finance.DashBoard.service;

import Finance.DashBoard.dto.UserPatchRequest;
import Finance.DashBoard.dto.UserRequest;
import Finance.DashBoard.dto.UserResponse;
import Finance.DashBoard.exception.BusinessException;
import Finance.DashBoard.model.User;
import Finance.DashBoard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email is already registered");
        }
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public UserResponse patchUser(Long id, UserPatchRequest patch) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));

        if (patch.getName() != null && !patch.getName().isBlank()) {
            user.setName(patch.getName().trim());
        }
        if (patch.getRole() != null) {
            user.setRole(patch.getRole());
        }
        if (patch.getIsActive() != null) {
            user.setIsActive(patch.getIsActive());
        }
        if (patch.getPassword() != null && !patch.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(patch.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive()
        );
    }
}
