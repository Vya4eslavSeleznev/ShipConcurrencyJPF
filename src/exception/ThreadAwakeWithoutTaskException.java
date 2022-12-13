package exception;

public class ThreadAwakeWithoutTaskException extends RuntimeException {

    public ThreadAwakeWithoutTaskException(String message) {
        super(message);
    }
}
