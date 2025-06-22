public class DataException extends Exception {
    private String message;

    public DataException(String msg) {
        super(msg);
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}