package errorhandling;

public class InternalErrorException extends Exception {
    public InternalErrorException(String errorMessage) {
        super(errorMessage);
    }
}
