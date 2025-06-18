public interface QuestionHandler {
    void processQuestion(Question question);
    boolean validateAnswer(Question question, String answer);
    String getQuestionType(Question question);
}
