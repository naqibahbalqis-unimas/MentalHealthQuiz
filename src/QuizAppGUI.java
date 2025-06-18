import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JavaFX version of the quiz application.
 * It displays a learning module first and then a series of quiz questions.
 */
public class QuizAppGUI extends Application {
    private Stage stage;
    private TextArea questionArea;
    private VBox optionsBox;
    private Button submitButton;

    private QuizModule quiz;
    private List<String> userAnswers;
    private int currentQuestionIndex = 0;

    private LearningModule learningModule;

    private static final int MOBILE_WIDTH = 394;
    private static final int MOBILE_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Mental Health Learning & Quiz");

        quiz = new QuizModule(120);
        userAnswers = new ArrayList<>();
        loadSampleQuestions();
        quiz.generateQuiz();

        learningModule = new LearningModule(() -> startQuiz());
        showLearningModule();
        stage.show();
    }

    private void showLearningModule() {
        Scene scene = new Scene(learningModule.getPane(), MOBILE_WIDTH, MOBILE_HEIGHT, Color.BEIGE);
        stage.setScene(scene);
    }

    private ToggleGroup toggleGroup;

    private void startQuiz() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: beige;");

        questionArea = new TextArea();
        questionArea.setWrapText(true);
        questionArea.setEditable(false);
        questionArea.setFont(Font.font("Times New Roman", 18));

        ScrollPane qScroll = new ScrollPane(questionArea);
        qScroll.setPrefHeight(120);
        qScroll.setFitToWidth(true);

        optionsBox = new VBox(5);
        ScrollPane optionsScroll = new ScrollPane(optionsBox);
        optionsScroll.setPrefHeight(400);
        optionsScroll.setFitToWidth(true);

        submitButton = new Button("Submit Answer");
        submitButton.setOnAction(e -> handleSubmit());

        VBox center = new VBox(10, qScroll, optionsScroll, submitButton);
        center.setAlignment(Pos.TOP_CENTER);
        root.setCenter(center);

        Scene scene = new Scene(root, MOBILE_WIDTH, MOBILE_HEIGHT, Color.BEIGE);
        stage.setScene(scene);
        showQuestion(currentQuestionIndex);
    }

    private void handleSubmit() {
        if (toggleGroup.getSelectedToggle() != null) {
            String answer = (String) toggleGroup.getSelectedToggle().getUserData();
            userAnswers.add(answer);
            currentQuestionIndex++;
            if (currentQuestionIndex < quiz.questions.size()) {
                showQuestion(currentQuestionIndex);
            } else {
                showResults();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an answer.");
            alert.showAndWait();
        }
    }

    private void showQuestion(int index) {
        Question q = quiz.questions.get(index);
        questionArea.setText("Question " + (index + 1) + " of " + quiz.questions.size() + "\n\n" + q.getQuestion());
        optionsBox.getChildren().clear();
        toggleGroup = new ToggleGroup();

        if (q instanceof MultipleChoiceQuestion) {
            List<String> opts = ((MultipleChoiceQuestion) q).getOptions();
            for (int i = 0; i < opts.size(); i++) {
                RadioButton rb = new RadioButton((char)('A' + i) + ") " + opts.get(i));
                rb.setUserData(opts.get(i));
                rb.setFont(Font.font("Times New Roman", 16));
                rb.setToggleGroup(toggleGroup);
                optionsBox.getChildren().add(rb);
            }
        } else if (q instanceof TrueFalseQuestion) {
            RadioButton trueBtn = new RadioButton("A) True");
            trueBtn.setUserData("true");
            RadioButton falseBtn = new RadioButton("B) False");
            falseBtn.setUserData("false");
            trueBtn.setFont(Font.font("Times New Roman", 16));
            falseBtn.setFont(Font.font("Times New Roman", 16));
            trueBtn.setToggleGroup(toggleGroup);
            falseBtn.setToggleGroup(toggleGroup);
            optionsBox.getChildren().addAll(trueBtn, falseBtn);
        }
    }

    private void showResults() {
        int score = quiz.evaluateAnswers(userAnswers);
        double percentage = quiz.calculateScore();
        String message = quiz.getMotivationalMessage();

        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: beige;");

        Label scoreLabel = new Label("Your Score: " + score + "/" + quiz.questions.size());
        scoreLabel.setFont(Font.font("Times New Roman", 24));
        Label percentLabel = new Label(String.format("Percentage: %.1f%%", percentage));
        percentLabel.setFont(Font.font("Times New Roman", 20));
        if (percentage >= 80) {
            percentLabel.setTextFill(Color.DARKGREEN);
        } else if (percentage >= 60) {
            percentLabel.setTextFill(Color.DARKORANGE);
        } else {
            percentLabel.setTextFill(Color.RED);
        }

        TextArea msg = new TextArea(message);
        msg.setWrapText(true);
        msg.setEditable(false);
        msg.setFont(Font.font("Times New Roman", 16));

        Button reviewBtn = new Button("Review Learning");
        reviewBtn.setOnAction(e -> {
            currentQuestionIndex = 0;
            userAnswers.clear();
            quiz.generateQuiz();
            learningModule.reset();
            showLearningModule();
        });

        Button retryBtn = new Button("Retake Quiz");
        retryBtn.setOnAction(e -> {
            currentQuestionIndex = 0;
            userAnswers.clear();
            quiz.generateQuiz();
            startQuiz();
        });

        HBox buttons = new HBox(10, reviewBtn, retryBtn);
        buttons.setAlignment(Pos.CENTER);

        root.getChildren().addAll(scoreLabel, percentLabel, msg, buttons);

        Scene scene = new Scene(root, MOBILE_WIDTH, MOBILE_HEIGHT, Color.BEIGE);
        stage.setScene(scene);
    }

    private void loadSampleQuestions() {
        quiz.addQuestion(new MultipleChoiceQuestion(
                "What is a common symptom of depression?",
                10,
                Arrays.asList("Fever", "Persistent sadness", "High energy", "Strong appetite"),
                "Persistent sadness"
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Burnout only affects people with low-paying jobs.",
                10,
                false
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "Which of the following can help manage anxiety?",
                10,
                Arrays.asList("Avoiding sleep", "Overworking", "Deep breathing", "Ignoring problems"),
                "Deep breathing"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "What should you do if someone talks about suicide?",
                10,
                Arrays.asList("Ignore them", "Tell them to be positive", "Listen and encourage professional help", "Challenge them"),
                "Listen and encourage professional help"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "What does CPTSD stand for?",
                10,
                Arrays.asList(
                        "Chronic Physical Trauma Stress Disorder",
                        "Complex Persistent Trauma Stress Disorder",
                        "Complex Post-Traumatic Stress Disorder",
                        "Chronic Psychological Therapy Stress Disorder"
                ),
                "Complex Post-Traumatic Stress Disorder"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "Which strategy is recommended to manage burnout?",
                10,
                Arrays.asList("Working longer hours", "Multitasking", "Taking regular breaks", "Suppressing emotions"),
                "Taking regular breaks"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "Which of these is NOT a symptom of depression?",
                10,
                Arrays.asList("Persistent sadness", "Loss of interest", "Increased appetite", "Improved concentration"),
                "Improved concentration"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "What is a healthy way to manage stress?",
                10,
                Arrays.asList("Overeating", "Mindfulness meditation", "Ignoring problems", "Binge drinking"),
                "Mindfulness meditation"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "Which neurotransmitter is often linked to depression?",
                10,
                Arrays.asList("Serotonin", "Adrenaline", "Dopamine", "Acetylcholine"),
                "Serotonin"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "What is an example of emotional numbing in CPTSD?",
                10,
                Arrays.asList("Excessive crying", "Avoiding emotions", "Anger outbursts", "Hyperactivity"),
                "Avoiding emotions"
        ));

        quiz.addQuestion(new MultipleChoiceQuestion(
                "How can physical activity help mental health?",
                10,
                Arrays.asList("Increases isolation", "Worsens insomnia", "Boosts mood and reduces anxiety", "Lowers oxygen"),
                "Boosts mood and reduces anxiety"
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Anxiety disorders are the most common type of mental illness worldwide.",
                10,
                true
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Talking about suicide increases the chances of someone taking their life.",
                10,
                false
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "CPTSD is the same as PTSD.",
                10,
                false
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Burnout can lead to physical symptoms like headaches and fatigue.",
                10,
                true
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Ignoring your feelings can improve mental health.",
                10,
                false
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Maintaining a sleep schedule helps with anxiety and depression.",
                10,
                true
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Only adults experience anxiety and depression.",
                10,
                false
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "It is normal to feel completely worthless when burned out.",
                10,
                false
        ));

        quiz.addQuestion(new TrueFalseQuestion(
                "Therapy is only for people with serious mental illnesses.",
                10,
                false
        ));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
