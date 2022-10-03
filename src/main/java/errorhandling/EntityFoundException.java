package errorhandling;

public class EntityFoundException extends Exception {
    public EntityFoundException(String errorMessage) {
        super(errorMessage);
    }
}
