package bg.emiliyan.acc_backend.exceptions;

public class GoogleAccountAlreadyLinkedException extends RuntimeException {
    public GoogleAccountAlreadyLinkedException() {
        super("Your account already has a Google account linked");
    }
}
