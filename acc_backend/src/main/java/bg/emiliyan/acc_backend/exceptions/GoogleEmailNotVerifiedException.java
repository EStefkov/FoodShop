package bg.emiliyan.acc_backend.exceptions;

public class GoogleEmailNotVerifiedException extends RuntimeException {
    public GoogleEmailNotVerifiedException() {
        super("Google email not verified");
    }
}
