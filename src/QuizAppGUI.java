import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class QuizAppGUI {
    private JFrame frame;
    private JTextArea questionArea;
    private JPanel optionsPanel;
    private JButton submitButton;
    private JLabel feedbackLabel;

    private QuizModule quiz;
    private java.util.List<String> userAnswers;
    private int currentQuestionIndex = 0;

    // Learning module
    private LearningModule learningModule;

    // Mobile preview dimensions (9:16 ratio)
    private static final int MOBILE_WIDTH = 394;
    private static final int MOBILE_HEIGHT = 700;

    public QuizAppGUI() {
        quiz = new QuizModule(120);
        userAnswers = new ArrayList<>();
        loadSampleQuestions();
        quiz.generateQuiz();
        createGUI();
        
        // Create learning module and show it first
        learningModule = new LearningModule(() -> startQuiz());
        showLearningModule();
    }

    private void createGUI() {
        frame = new JFrame("Mental Health Learning & Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(MOBILE_WIDTH, MOBILE_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Initialize quiz components (will be used later)
        questionArea = new JTextArea(3, 30);
        questionArea.setWrapStyleWord(true);
        questionArea.setLineWrap(true);
        questionArea.setEditable(false);
        questionArea.setFont(new Font("Times New Roman", Font.BOLD, 18));
        questionArea.setBackground(frame.getBackground());

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        submitButton = new JButton("Submit Answer");
        submitButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        submitButton.setPreferredSize(new Dimension(200, 40));

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonModel selected = buttonGroup.getSelection();
                if (selected != null) {
                    userAnswers.add(selected.getActionCommand());
                    currentQuestionIndex++;
                    if (currentQuestionIndex < quiz.questions.size()) {
                        showQuestion(currentQuestionIndex);
                    } else {
                        showResults();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an answer.");
                }
            }
        });

        frame.setVisible(true);
    }

    private void showLearningModule() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(learningModule.getPanel());
        frame.revalidate();
        frame.repaint();
    }

    private void startQuiz() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create scrollable question area
        JScrollPane questionScroll = new JScrollPane(questionArea);
        questionScroll.setPreferredSize(new Dimension(MOBILE_WIDTH - 40, 120));
        questionScroll.setBorder(BorderFactory.createTitledBorder("Question"));
        
        // Create scrollable options area
        JScrollPane optionsScroll = new JScrollPane(optionsPanel);
        optionsScroll.setPreferredSize(new Dimension(MOBILE_WIDTH - 40, 400));
        optionsScroll.setBorder(BorderFactory.createTitledBorder("Choose your answer"));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);

        mainPanel.add(questionScroll, BorderLayout.NORTH);
        mainPanel.add(optionsScroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(mainPanel);
        frame.revalidate();
        frame.repaint();
        
        showQuestion(currentQuestionIndex);
    }

    private ButtonGroup buttonGroup;

    private void showQuestion(int index) {
        Question q = quiz.questions.get(index);
        questionArea.setText("Question " + (index + 1) + " of " + quiz.questions.size() + ":\n\n" + q.getQuestion());
        optionsPanel.removeAll();

        buttonGroup = new ButtonGroup();

        if (q instanceof MultipleChoiceQuestion) {
            java.util.List<String> options = ((MultipleChoiceQuestion) q).getOptions();
            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i);
                JRadioButton btn = new JRadioButton((char)('A' + i) + ") " + option);
                btn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
                btn.setActionCommand(option);
                btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                buttonGroup.add(btn);
                
                optionsPanel.add(Box.createVerticalStrut(5));
                optionsPanel.add(btn);
            }
        } else if (q instanceof TrueFalseQuestion) {
            JRadioButton trueBtn = new JRadioButton("A) True");
            trueBtn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
            trueBtn.setActionCommand("true");
            trueBtn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JRadioButton falseBtn = new JRadioButton("B) False");
            falseBtn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
            falseBtn.setActionCommand("false");
            falseBtn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            buttonGroup.add(trueBtn);
            buttonGroup.add(falseBtn);
            
            optionsPanel.add(Box.createVerticalStrut(5));
            optionsPanel.add(trueBtn);
            optionsPanel.add(Box.createVerticalStrut(5));
            optionsPanel.add(falseBtn);
        }

        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void showResults() {
        int score = quiz.evaluateAnswers(userAnswers);
        double percentage = quiz.calculateScore();
        String message = quiz.getMotivationalMessage();

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel with score
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel scoreLabel = new JLabel("Your Score: " + score + "/" + quiz.questions.size(), JLabel.CENTER);
        scoreLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        JLabel percentageLabel = new JLabel(String.format("Percentage: %.1f%%", percentage), JLabel.CENTER);
        percentageLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        
        // Color code the percentage
        if (percentage >= 80) {
            percentageLabel.setForeground(new Color(0, 128, 0)); // Green
        } else if (percentage >= 60) {
            percentageLabel.setForeground(new Color(255, 140, 0)); // Orange
        } else {
            percentageLabel.setForeground(Color.RED);
        }

        headerPanel.add(scoreLabel);
        headerPanel.add(percentageLabel);

        // Message area
        JTextArea messageArea = new JTextArea(message);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        messageArea.setBackground(frame.getBackground());
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setBorder(BorderFactory.createTitledBorder("Feedback"));

        // Restart buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton restartLearningButton = new JButton("Review Learning");
        restartLearningButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        restartLearningButton.addActionListener(e -> {
            currentQuestionIndex = 0;
            userAnswers.clear();
            quiz.generateQuiz();
            learningModule.reset(); // Reset to first page
            showLearningModule();
        });

        JButton retakeQuizButton = new JButton("Retake Quiz");
        retakeQuizButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        retakeQuizButton.addActionListener(e -> {
            currentQuestionIndex = 0;
            userAnswers.clear();
            quiz.generateQuiz();
            startQuiz();
        });

        buttonPanel.add(restartLearningButton);
        buttonPanel.add(retakeQuizButton);

        resultPanel.add(headerPanel, BorderLayout.NORTH);
        resultPanel.add(messageScroll, BorderLayout.CENTER);
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(resultPanel);
        frame.revalidate();
        frame.repaint();
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
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new QuizAppGUI();
        });
    }
}