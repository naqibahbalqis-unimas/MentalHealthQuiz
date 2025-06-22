public class ContentException extends Exception {
    private String message;

    public ContentException(String msg) {
        super(msg);
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}