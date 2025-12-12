package bg.emiliyan.acc_backend.exceptions;

public class UserNotFoundByNameException extends RuntimeException {
    public UserNotFoundByNameException(String name) {
        super("User not found with username/email: " + name );
    }
}
