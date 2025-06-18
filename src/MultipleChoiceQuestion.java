import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private List<String> options;
    private String correctOption;

    public MultipleChoiceQuestion(String questionText, int points, List<String> options, String correctOption) {
        super(questionText, points);
        this.options = options;
        this.correctOption = correctOption;
    }

    public List<String> getOptions() {
        return options;
    }

    @Override
    public boolean evaluate(String answer) {
        return correctOption.equalsIgnoreCase(answer);
    }
}
