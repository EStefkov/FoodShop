package bg.emiliyan.acc_backend.exceptions;

public class GoogleAccountAlreadyLinked extends RuntimeException {
    public GoogleAccountAlreadyLinked() {
        super("This Google account is already linked to another user");
    }

}
