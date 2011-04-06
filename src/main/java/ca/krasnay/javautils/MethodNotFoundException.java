package ca.krasnay.javautils;

public class MethodNotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public MethodNotFoundException() {
    }

    public MethodNotFoundException(String s) {
        super(s);
    }

    public MethodNotFoundException(Throwable cause) {
        super(cause);
    }

    public MethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
