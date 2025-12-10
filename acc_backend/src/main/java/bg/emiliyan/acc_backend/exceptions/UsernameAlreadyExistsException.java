package bg.emiliyan.acc_backend.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("\"Registration failed. Please check your input or try a different username/email.\"");
    }

}
