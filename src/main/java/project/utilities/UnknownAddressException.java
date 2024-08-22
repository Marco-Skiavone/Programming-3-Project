package project.utilities;

/** RuntimeException thrown whenever a request is sent by someone who is not authorized.
 * Only the LogIn request will not throw this Exception, due to its nature of checking addresses. */
public class UnknownAddressException extends RuntimeException {
    public UnknownAddressException(String message) {
        super(message);
    }
    @Override
    public String getMessage() {
        return "UnknownAddressException occurred due to unknown address trying to send a request.";
    }
}
