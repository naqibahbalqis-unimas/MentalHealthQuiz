// QuizModule.java
public class QuizModule {
    private GamificationEngine gamificationEngine;

    public QuizModule(GamificationEngine engine) {
        this.gamificationEngine = engine;
    }

    public void awardScoreToUser(User user, int correctAnswers) {
        gamificationEngine.awardPointsToUser(user, correctAnswers);
    }
}

