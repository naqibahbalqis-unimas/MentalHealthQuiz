import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Swing version of the quiz application.
 * It displays a learning module first and then a series of quiz questions.
 */
public class QuizAppGUI {
    private JFrame frame;
    private JTextArea questionArea;
    private JPanel optionsPanel;
    private JButton submitButton;

    private QuizModule quiz;
    private List<String> userAnswers;
    private int currentQuestionIndex = 0;

    private LearningModule learningModule;

    private static final int MOBILE_WIDTH = 394;
    private static final int MOBILE_HEIGHT = 700;

    public QuizAppGUI() {
        frame = new JFrame("Mental Health Learning & Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(MOBILE_WIDTH, MOBILE_HEIGHT);

        quiz = new QuizModule(120);
        userAnswers = new ArrayList<>();
        loadSampleQuestions();
        quiz.generateQuiz();

        learningModule = new LearningModule(() -> startQuiz());
        showLearningModule();
        frame.setVisible(true);
    }

    private void showLearningModule() {
        frame.setContentPane(learningModule.getPane());
        frame.revalidate();
    }

    private ButtonGroup toggleGroup;

    private void startQuiz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 245, 220));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        questionArea = new JTextArea();
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setEditable(false);
        questionArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        JScrollPane qScroll = new JScrollPane(questionArea);
        qScroll.setPreferredSize(new Dimension(MOBILE_WIDTH - 20, 120));

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        JScrollPane optionsScroll = new JScrollPane(optionsPanel);
        optionsScroll.setPreferredSize(new Dimension(MOBILE_WIDTH - 20, 400));

        submitButton = new JButton("Submit Answer");
        submitButton.addActionListener(e -> handleSubmit());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(qScroll);
        center.add(optionsScroll);
        center.add(submitButton);

        root.add(center, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.revalidate();
        showQuestion(currentQuestionIndex);
    }

    private void handleSubmit() {
        if (toggleGroup.getSelection() != null) {
            String answer = toggleGroup.getSelection().getActionCommand();
            userAnswers.add(answer);
            currentQuestionIndex++;
            if (currentQuestionIndex < quiz.questions.size()) {
                showQuestion(currentQuestionIndex);
            } else {
                showResults();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an answer.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showQuestion(int index) {
        Question q = quiz.questions.get(index);
        questionArea.setText("Question " + (index + 1) + " of " + quiz.questions.size() + "\n\n" + q.getQuestion());
        optionsPanel.removeAll();
        toggleGroup = new ButtonGroup();

        if (q instanceof MultipleChoiceQuestion) {
            List<String> opts = ((MultipleChoiceQuestion) q).getOptions();
            for (int i = 0; i < opts.size(); i++) {
                JRadioButton rb = new JRadioButton((char)('A' + i) + ") " + opts.get(i));
                rb.setActionCommand(opts.get(i));
                rb.setFont(new Font("Times New Roman", Font.PLAIN, 16));
                toggleGroup.add(rb);
                optionsPanel.add(rb);
            }
        } else if (q instanceof TrueFalseQuestion) {
            JRadioButton trueBtn = new JRadioButton("A) True");
            trueBtn.setActionCommand("true");
            JRadioButton falseBtn = new JRadioButton("B) False");
            falseBtn.setActionCommand("false");
            trueBtn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
            falseBtn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
            toggleGroup.add(trueBtn);
            toggleGroup.add(falseBtn);
            optionsPanel.add(trueBtn);
            optionsPanel.add(falseBtn);
        }
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void showResults() {
        int score = quiz.evaluateAnswers(userAnswers);
        double percentage = quiz.calculateScore();
        String message = quiz.getMotivationalMessage();

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(245, 245, 220));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel scoreLabel = new JLabel("Your Score: " + score + "/" + quiz.questions.size());
        scoreLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        JLabel percentLabel = new JLabel(String.format("Percentage: %.1f%%", percentage));
        percentLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        if (percentage >= 80) {
            percentLabel.setForeground(new Color(0, 100, 0));
        } else if (percentage >= 60) {
            percentLabel.setForeground(new Color(255, 140, 0));
        } else {
            percentLabel.setForeground(Color.RED);
        }

        JTextArea msg = new JTextArea(message);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setEditable(false);
        msg.setFont(new Font("Times New Roman", Font.PLAIN, 16));

        JButton reviewBtn = new JButton("Review Learning");
        reviewBtn.addActionListener(e -> {
            currentQuestionIndex = 0;
            userAnswers.clear();
            quiz.generateQuiz();
            learningModule.reset();
            showLearningModule();
        });

        JButton retryBtn = new JButton("Retake Quiz");
        retryBtn.addActionListener(e -> {
            currentQuestionIndex = 0;
            userAnswers.clear();
            quiz.generateQuiz();
            startQuiz();
        });

        JPanel buttons = new JPanel();
        buttons.add(reviewBtn);
        buttons.add(retryBtn);

        root.add(scoreLabel);
        root.add(percentLabel);
        root.add(msg);
        root.add(buttons);

        frame.setContentPane(root);
        frame.revalidate();
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
        SwingUtilities.invokeLater(QuizAppGUI::new);
    }
}
