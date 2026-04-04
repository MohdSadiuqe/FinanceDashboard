package Finance.DashBoard.dto;

import Finance.DashBoard.model.Role;
import lombok.Data;

@Data
public class UserPatchRequest {

    private String name;
    private Role role;
    private Boolean isActive;
    /** When set, replaces password (BCrypt hashed). Admin-only use. */
    private String password;
}
