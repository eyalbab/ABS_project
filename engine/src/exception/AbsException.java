package exception;

public class AbsException extends Exception {
    private String errorMsg;

    public AbsException(String error) {
        super(error);
        errorMsg = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
