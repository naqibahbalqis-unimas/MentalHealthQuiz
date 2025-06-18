import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
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

    public QuizAppGUI() {
        quiz = new QuizModule(120);
        userAnswers = new ArrayList<>();
        loadSampleQuestions();
        quiz.generateQuiz();
        createGUI();
        showQuestion(currentQuestionIndex);
    }

    private void createGUI() {
        frame = new JFrame("Mental Health Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(540, 1200); 

        questionArea = new JTextArea(3, 30);
        questionArea.setWrapStyleWord(true);
        questionArea.setLineWrap(true);
        questionArea.setEditable(false);
        questionArea.setFont(new Font("Times New Roman", Font.BOLD, 20));

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        submitButton = new JButton("Submit Answer");
        feedbackLabel = new JLabel(" ");

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

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new JScrollPane(questionArea), BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(optionsPanel), BorderLayout.CENTER);
        mainPanel.add(submitButton, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private ButtonGroup buttonGroup;

    private void showQuestion(int index) {
        Question q = quiz.questions.get(index);
        questionArea.setText("Q" + (index + 1) + ": " + q.getQuestion());
        optionsPanel.removeAll();

        buttonGroup = new ButtonGroup();

        if (q instanceof MultipleChoiceQuestion) {
            java.util.List<String> options = ((MultipleChoiceQuestion) q).getOptions();
            for (String option : options) {
                JRadioButton btn = new JRadioButton(option);
                btn.setFont(new Font("Times New Roman", Font.PLAIN, 24)); 
                btn.setActionCommand(option);
                buttonGroup.add(btn);
                optionsPanel.add(Box.createVerticalStrut(10));
                optionsPanel.add(btn);
            }
        } else if (q instanceof TrueFalseQuestion) {
            JRadioButton trueBtn = new JRadioButton("True");
            trueBtn.setFont(new Font("Times New Roman", Font.PLAIN, 24));
            trueBtn.setActionCommand("true");

            JRadioButton falseBtn = new JRadioButton("False");
            falseBtn.setFont(new Font("Times New Roman", Font.PLAIN, 24));
            falseBtn.setActionCommand("false");

            buttonGroup.add(trueBtn);
            buttonGroup.add(falseBtn);
            optionsPanel.add(Box.createVerticalStrut(10));
            optionsPanel.add(trueBtn);
            optionsPanel.add(Box.createVerticalStrut(10));
            optionsPanel.add(falseBtn);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void showResults() {
        int score = quiz.evaluateAnswers(userAnswers);
        double percentage = quiz.calculateScore();
        String message = quiz.getMotivationalMessage();

        JOptionPane.showMessageDialog(frame,
                "Your Score: " + score + "\n" +
                "Percentage: " + percentage + "%\n" +
                message,
                "Quiz Results",
                JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
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
        SwingUtilities.invokeLater(() -> new QuizAppGUI());
    }
}
