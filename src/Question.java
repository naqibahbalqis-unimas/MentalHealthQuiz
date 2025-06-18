public abstract class Question {
    protected String questionText;
    protected int points;

    public Question(String questionText, int points) {
        this.questionText = questionText;
        this.points = points;
    }

    public String getQuestion() {
        return questionText;
    }

    public int getPoints() {
        return points;
    }

    public abstract boolean evaluate(String answer);
}
