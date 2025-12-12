package bg.emiliyan.acc_backend.exceptions;

public class UserNotFoundByIdException extends RuntimeException {
    public UserNotFoundByIdException(Long id) {
        super("User with id " + id + " not found");
    }
}
