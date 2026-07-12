package bg.emiliyan.acc_backend.exceptions;

/**
 * Thrown when a Google login matches an existing account by email, but that
 * account has not explicitly linked its Google identity yet.
 *
 * We deliberately refuse to auto-link in this case: auto-linking on email match
 * is vulnerable to account pre-hijacking (an attacker registers a normal account
 * with the victim's email first, then silently gains a parallel login path once
 * the victim signs in with Google). Linking must go through an authenticated flow
 * (see UserService#linkGoogleAccount) where the user proves ownership first.
 */
public class GoogleAccountLinkRequiredException extends RuntimeException {
    public GoogleAccountLinkRequiredException() {
        super("An account with this email already exists. Please log in with your password " +
                "and link your Google account from your profile settings.");
    }
}
