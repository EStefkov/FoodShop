package bg.emiliyan.acc_backend.exceptions;

public class LastAdminException extends RuntimeException {
    public LastAdminException(String message) {
        super("Cannot delete the last ADMIN user");
    }
}
