package errorhandling;

public class InternalErrorException extends Exception {
    public InternalErrorException() {
        super("Internal Server Problem. We are sorry for the inconvenience.");
    }

    public InternalErrorException(String errorMessage) {
        super(errorMessage);
    }
}
