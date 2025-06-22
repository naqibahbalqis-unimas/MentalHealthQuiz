public class QuizException extends Exception {
    private String message;

    public QuizException(String msg) {
        super(msg);
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}