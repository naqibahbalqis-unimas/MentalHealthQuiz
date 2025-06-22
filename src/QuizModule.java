import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Data management classes

public class QuizModule implements QuestionHandler {
    public List<Question> questions;
    private int currentScore;
    private int totalQuestions;
    private int timeLimit; // in seconds
    private GamificationEngine engine;
    private DatabaseHandler dataManager;

    public QuizModule(int timeLimit) {
        this(timeLimit, null, null);
    }

    public QuizModule(int timeLimit, GamificationEngine engine, DatabaseHandler dataManager) {
        this.timeLimit = timeLimit;
        this.engine = engine;
        this.dataManager = dataManager;
        this.questions = new ArrayList<>();
        this.currentScore = 0;
    }

    // Constructor used by the gamification GUI
    public QuizModule(GamificationEngine engine) {
        this(0, engine, null);
    }

    public QuizModule(GamificationEngine engine, DatabaseHandler dataManager) {
        this(0, engine, dataManager);
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void generateQuiz() {
        shuffleQuestions();
        totalQuestions = questions.size();
        System.out.println("Quiz Started. Time limit: " + timeLimit + " seconds");
    }

    public int evaluateAnswers(List<String> answers) {
        currentScore = 0;
        for (int i = 0; i < answers.size(); i++) {
            Question q = questions.get(i);
            if (q.evaluate(answers.get(i))) {
                currentScore += q.getPoints();
            }
        }
        persistScore();
        return currentScore;
    }

    // Award points for the given number of correct answers using the
    // associated gamification engine, if available.
    public void awardScoreToUser(User user, int correctAnswers) {
        if (engine != null) {
            engine.awardPointsToUser(user, correctAnswers);
        }
    }

    public double calculateScore() {
        return ((double) currentScore / (totalQuestions * 10)) * 100;
    }

    public String getMotivationalMessage() {
        if (calculateScore() >= 80) return "Excellent job! Keep it up!";
        else if (calculateScore() >= 50) return "Good effort! You can do even better!";
        else return "Don’t give up! Learning takes time.";
    }

    private void shuffleQuestions() {
        Collections.shuffle(questions);
    }

    @Override
    public void processQuestion(Question question) {
        System.out.println("Processing: " + question.getQuestion());
    }

    @Override
    public boolean validateAnswer(Question question, String answer) {
        return question.evaluate(answer);
    }

    @Override
    public String getQuestionType(Question question) {
        if (question instanceof MultipleChoiceQuestion) return "MCQ";
        if (question instanceof TrueFalseQuestion) return "True/False";
        return "Unknown";
    }

    // Save the current score percentage using the data manager if available
    private void persistScore() {
        if (dataManager != null && totalQuestions > 0) {
            int percentage = (int) Math.round(calculateScore());
            try {
                dataManager.appendScore(percentage);
            } catch (DataAccessException e) {
                System.err.println("Failed to store score: " + e.getMessage());
            }
        }
    }
}
