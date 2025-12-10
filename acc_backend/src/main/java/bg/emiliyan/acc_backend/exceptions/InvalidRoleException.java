package bg.emiliyan.acc_backend.exceptions;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String role) {
        super("Role '" + role + "' is invalid. Allowed roles: ADMIN, OWNER, USER");
    }
}