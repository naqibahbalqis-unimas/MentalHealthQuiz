import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;

/**
 * Swing version of the quiz application with integrated gamification.
 * It displays a learning module first, then a series of quiz questions,
 * followed by gamification results (badges and leaderboard) all in the same window.
 */
public class QuizAppGUI {
    private static List<User> allUsers = new ArrayList<>(); // Global user list for leaderboard
    
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private JTextArea questionArea;
    private JPanel optionsPanel;
    private JButton submitButton;

    private QuizModule quiz;
    private GamificationEngine gamificationEngine;
    private List<String> userAnswers;
    private int currentQuestionIndex = 0;
    private User currentUser;

    private LearningModule learningModule;
    private ButtonGroup toggleGroup;

    private static final int MOBILE_WIDTH = 394;
    private static final int MOBILE_HEIGHT = 700;

    public QuizAppGUI() {
        // Get user name at startup
        getCurrentUserInfo();
        
        frame = new JFrame("Mental Health Learning & Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(MOBILE_WIDTH, MOBILE_HEIGHT);
        frame.setLocationRelativeTo(null);

        // Initialize gamification engine
        gamificationEngine = new GamificationEngine();
        
        quiz = new QuizModule(120);
        userAnswers = new ArrayList<>();
        loadSampleQuestions();
        quiz.generateQuiz();

        // Setup CardLayout for different screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        learningModule = new LearningModule(() -> startQuiz());
        
        // Add all panels to CardLayout
        mainPanel.add(learningModule.getPane(), "learning");
        mainPanel.add(createQuizPanel(), "quiz");
        mainPanel.add(createResultsPanel(), "results");
        mainPanel.add(createGamificationPanel(), "gamification");
        mainPanel.add(createLeaderboardPanel(), "leaderboard");
        
        // Start with learning module
        cardLayout.show(mainPanel, "learning");
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Collect user information before starting the application
     */
    private void getCurrentUserInfo() {
        String userName = JOptionPane.showInputDialog(
            null, 
            "Welcome to Mental Health Awareness!\nPlease enter your name:", 
            "User Registration", 
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (userName == null || userName.trim().isEmpty()) {
            userName = "Anonymous User";
        }
        
        currentUser = new User(userName.trim());
    }

    private JPanel createQuizPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 245, 220));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // User info at the top
        JLabel userLabel = new JLabel("User: " + currentUser.getName(), SwingConstants.CENTER);
        userLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

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
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(qScroll);
        center.add(optionsScroll);
        center.add(Box.createVerticalStrut(10)); // Add some spacing
        center.add(submitButton);

        root.add(userLabel, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        return root;
    }

    private JPanel createResultsPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(245, 245, 220));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Quiz Completed!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("", SwingConstants.CENTER);
        userLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel percentLabel = new JLabel("", SwingConstants.CENTER);
        percentLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea msg = new JTextArea();
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setEditable(false);
        msg.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setMaximumSize(new Dimension(350, 100));
        msg.setBackground(root.getBackground());

        JButton viewGamificationBtn = new JButton("View Badge & Leaderboard");
        viewGamificationBtn.setFont(new Font("Times New Roman", Font.BOLD, 16));
        viewGamificationBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewGamificationBtn.addActionListener(e -> cardLayout.show(mainPanel, "gamification"));

        JButton reviewBtn = new JButton("Review Learning");
        reviewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        reviewBtn.addActionListener(e -> {
            resetQuiz();
            cardLayout.show(mainPanel, "learning");
        });

        JButton retryBtn = new JButton("Retake Quiz");
        retryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        retryBtn.addActionListener(e -> {
            resetQuiz();
            startQuiz();
        });

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setBackground(root.getBackground());
        buttons.add(Box.createVerticalStrut(10));
        buttons.add(viewGamificationBtn);
        buttons.add(Box.createVerticalStrut(5));
        buttons.add(reviewBtn);
        buttons.add(Box.createVerticalStrut(5));
        buttons.add(retryBtn);

        root.add(titleLabel);
        root.add(Box.createVerticalStrut(10));
        root.add(userLabel);
        root.add(Box.createVerticalStrut(10));
        root.add(scoreLabel);
        root.add(Box.createVerticalStrut(5));
        root.add(percentLabel);
        root.add(Box.createVerticalStrut(15));
        root.add(msg);
        root.add(Box.createVerticalStrut(20));
        root.add(buttons);

        // Store references to update later
        root.putClientProperty("userLabel", userLabel);
        root.putClientProperty("scoreLabel", scoreLabel);
        root.putClientProperty("percentLabel", percentLabel);
        root.putClientProperty("messageArea", msg);

        return root;
    }

    private JPanel createGamificationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Congratulations!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Badge image
        JLabel badgeImage = new JLabel();
        badgeImage.setHorizontalAlignment(SwingConstants.CENTER);
        badgeImage.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Result text area
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        resultArea.setBackground(panel.getBackground());
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(panel.getBackground());
        
        JButton viewLeaderboardBtn = new JButton("View Leaderboard");
        viewLeaderboardBtn.setFont(new Font("Times New Roman", Font.BOLD, 14));
        viewLeaderboardBtn.addActionListener(e -> cardLayout.show(mainPanel, "leaderboard"));
        
        JButton backToResultsBtn = new JButton("Back to Results");
        backToResultsBtn.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        backToResultsBtn.addActionListener(e -> cardLayout.show(mainPanel, "results"));
        
        JButton takeQuizAgainBtn = new JButton("Take Quiz Again");
        takeQuizAgainBtn.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        takeQuizAgainBtn.addActionListener(e -> {
            resetQuiz();
            startQuiz();
        });

        buttonPanel.add(viewLeaderboardBtn);
        buttonPanel.add(backToResultsBtn);
        buttonPanel.add(takeQuizAgainBtn);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(panel.getBackground());
        contentPanel.add(badgeImage, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Store references for updates
        panel.putClientProperty("badgeImage", badgeImage);
        panel.putClientProperty("resultArea", resultArea);

        return panel;
    }

    private JPanel createLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title with banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(panel.getBackground());
        
        JLabel titleLabel = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel bannerLabel = new JLabel();
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String bannerPath = "assets" + File.separator + "badges" + File.separator + "Leaderboard.png";
        try {
            ImageIcon bannerIcon = new ImageIcon(bannerPath);
            Image img = bannerIcon.getImage();
            
            // Calculate scaled dimensions maintaining aspect ratio
            int originalWidth = img.getWidth(null);
            int originalHeight = img.getHeight(null);
            int maxWidth = 250;
            int maxHeight = 100;
            
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);
            
            int scaledWidth = (int) (originalWidth * ratio);
            int scaledHeight = (int) (originalHeight * ratio);
            
            Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            bannerLabel.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            bannerLabel.setText("LEADERBOARD");
            bannerLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        }

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(bannerLabel, BorderLayout.CENTER);

        // Use JEditorPane for HTML content (supports images)
        JEditorPane leaderboardPane = new JEditorPane();
        leaderboardPane.setContentType("text/html");
        leaderboardPane.setEditable(false);
        leaderboardPane.setBackground(Color.WHITE);
        leaderboardPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(leaderboardPane);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Rankings"));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(panel.getBackground());
        
        JButton backBtn = new JButton("Back to Badge");
        backBtn.setFont(new Font("Times New Roman", Font.BOLD, 14));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "gamification"));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        refreshBtn.addActionListener(e -> updateLeaderboardContentHTML(leaderboardPane));

        buttonPanel.add(backBtn);
        buttonPanel.add(refreshBtn);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Store reference for updates
        panel.putClientProperty("leaderboardPane", leaderboardPane);

        return panel;
    }

    // New method for HTML-based leaderboard with images
    private void updateLeaderboardContentHTML(JEditorPane leaderboardPane) {
        allUsers.sort((u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());
        StringBuilder html = new StringBuilder();
        
        html.append("<html><body style='font-family: monospaced; font-size: 12px;'>");
        html.append("<table border='1' cellpadding='5' cellspacing='0' width='100%'>");
        html.append("<tr style='background-color: #f0f0f0; font-weight: bold;'>");
        html.append("<th>Rank</th><th>Name</th><th>Points</th><th>Badge</th></tr>");
        
        int rank = 1;
        for (User u : allUsers) {
            html.append("<tr>");
            
            // Rank with image or emoji
            html.append("<td align='center'>");
            html.append(getRankSymbolHTML(rank));
            html.append(rank);
            html.append("</td>");
            
            // Name
            String displayName = u.getName().length() > 13 ? u.getName().substring(0, 13) + ".." : u.getName();
            html.append("<td>").append(displayName).append("</td>");
            
            // Points
            html.append("<td align='center'>").append(u.getTotalPoints()).append("</td>");
            
            // Badge with image
            html.append("<td align='center'>");
            html.append(getBadgeHTML(u));
            html.append("</td>");
            
            html.append("</tr>");
            rank++;
        }
        
        if (allUsers.isEmpty()) {
            html.append("<tr><td colspan='4' align='center'>No users yet!</td></tr>");
        }
        
        html.append("</table></body></html>");
        leaderboardPane.setText(html.toString());
    }

    // Get rank symbol as HTML (with images if available)
    private String getRankSymbolHTML(int rank) {
        String basePath = "assets" + File.separator + "badges" + File.separator;
        switch (rank) {
            case 1: 
                // Try to use Gold badge as rank 1 symbol
                return createImageHTML(basePath + "Gold.png", 20, 20);
                
            case 2: 
                // Try to use Silver badge as rank 2 symbol  
                return createImageHTML(basePath + "Silver.png", 20, 20);
                
            case 3: 
                // Try to use Bronze badge as rank 3 symbol
                return createImageHTML(basePath + "Bronze.png", 20, 20);
                
            default: 
                return "";
        }
    }

    // Get badge as HTML image
    private String getBadgeHTML(User user) {
        if (user.getBadge() != null && user.getBadgeIconPath() != null) {
            String imagePath = user.getBadgeIconPath();
            String imageHTML = createImageHTML(imagePath, 25, 25);
            if (!imageHTML.isEmpty()) {
                return imageHTML;
            }
        }
        // Fallback to text
        return user.getBadgeName();
    }

    private void startQuiz() {
        cardLayout.show(mainPanel, "quiz");
        showQuestion(currentQuestionIndex);
    }

    private void resetQuiz() {
        currentQuestionIndex = 0;
        userAnswers.clear();
        quiz.generateQuiz();
        learningModule.reset();
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
        // Calculate the quiz results
        int correctAnswers = 0;
        for (int i = 0; i < userAnswers.size(); i++) {
            Question q = quiz.questions.get(i);
            if (q.evaluate(userAnswers.get(i))) {
                correctAnswers++;
            }
        }
        
        final int finalCorrectAnswers = correctAnswers;
        
        int totalScore = quiz.evaluateAnswers(userAnswers);
        double percentage = quiz.calculateScore();
        String message = quiz.getMotivationalMessage();

        // Update results panel
        JPanel resultsPanel = (JPanel) getComponentByName("results");
        JLabel userLabel = (JLabel) resultsPanel.getClientProperty("userLabel");
        JLabel scoreLabel = (JLabel) resultsPanel.getClientProperty("scoreLabel");
        JLabel percentLabel = (JLabel) resultsPanel.getClientProperty("percentLabel");
        JTextArea msgArea = (JTextArea) resultsPanel.getClientProperty("messageArea");

        userLabel.setText("User: " + currentUser.getName());
        scoreLabel.setText("Correct Answers: " + finalCorrectAnswers + "/" + quiz.questions.size());
        percentLabel.setText(String.format("Percentage: %.1f%%", percentage));
        
        if (percentage >= 80) {
            percentLabel.setForeground(new Color(0, 100, 0));
        } else if (percentage >= 60) {
            percentLabel.setForeground(new Color(255, 140, 0));
        } else {
            percentLabel.setForeground(Color.RED);
        }
        
        msgArea.setText(message);

        // Process gamification
        gamificationEngine.addUser(currentUser);
        allUsers.add(currentUser);
        gamificationEngine.awardPointsToUser(currentUser, finalCorrectAnswers);

        // Update gamification panel
        updateGamificationPanel();
        
        // Show results
        cardLayout.show(mainPanel, "results");
    }

    private void updateGamificationPanel() {
        JPanel gamificationPanel = (JPanel) getComponentByName("gamification");
        JLabel badgeImage = (JLabel) gamificationPanel.getClientProperty("badgeImage");
        JTextArea resultArea = (JTextArea) gamificationPanel.getClientProperty("resultArea");

        // Update content
        StringBuilder sb = new StringBuilder();
        sb.append("QUIZ RESULTS\n");
        sb.append("═".repeat(30)).append("\n\n");
        sb.append("Name: ").append(currentUser.getName()).append("\n\n");
        sb.append("Points Earned: ").append(currentUser.getTotalPoints()).append(" points\n\n");
        sb.append("Badge Achieved: ").append(currentUser.getBadgeName()).append("\n\n");
        
        if (currentUser.getBadge() != null) {
            sb.append("Requirements: ").append(currentUser.getBadge().getRequirement()).append("\n\n");
        }
        
        String motivation = getMotivationalMessage(currentUser.getBadgeName());
        sb.append("Message: ").append(motivation);
        
        resultArea.setText(sb.toString());

        // Display badge image
        String badgeIconPath = currentUser.getBadgeIconPath();
        if (badgeIconPath != null && !badgeIconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(badgeIconPath);
                Image img = icon.getImage();
                
                int originalWidth = img.getWidth(null);
                int originalHeight = img.getHeight(null);
                int maxSize = 120;
                double ratio = Math.min((double) maxSize / originalWidth, (double) maxSize / originalHeight);
                
                int scaledWidth = (int) (originalWidth * ratio);
                int scaledHeight = (int) (originalHeight * ratio);
                
                Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                badgeImage.setIcon(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                badgeImage.setText("Badge: " + currentUser.getBadgeName());
                badgeImage.setFont(new Font("Times New Roman", Font.BOLD, 16));
            }
        }

        // Update leaderboard
        JPanel leaderboardPanel = (JPanel) getComponentByName("leaderboard");
        JEditorPane leaderboardPane = (JEditorPane) leaderboardPanel.getClientProperty("leaderboardPane");
        updateLeaderboardContentHTML(leaderboardPane);
    }

    private void updateLeaderboardContent(JTextArea leaderboardArea) {
        leaderboardArea.setText("");
        allUsers.sort((u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("%-5s %-15s %-8s %-12s%n", "Rank", "Name", "Points", "Badge"));
        sb.append("─".repeat(45)).append("\n");
        
        int rank = 1;
        for (User u : allUsers) {
            String rankSymbol = getRankSymbol(rank);
            sb.append(String.format("%-5s %-15s %-8d %-12s%n",
                rankSymbol + rank, 
                u.getName().length() > 13 ? u.getName().substring(0, 13) + ".." : u.getName(),
                u.getTotalPoints(), 
                u.getBadgeName()
            ));
            rank++;
        }
        
        if (allUsers.isEmpty()) {
            sb.append("\n").append(" ".repeat(15)).append("No users yet!");
        }
        
        leaderboardArea.setText(sb.toString());
    }

    private String getRankSymbol(int rank) {
        // You can use image assets instead of text
        String basePath = "assets" + File.separator + "badges" + File.separator;
        switch (rank) {
            case 1: 
                // Option 1: Use existing Gold badge as rank 1 symbol
                // return createImageHTML(basePath + "Gold.png", 16, 16) + " ";
                
                // Option 2: Use text (current)
                return "1st ";
                
                // Option 3: Use custom rank images (if you create them)
                // return createImageHTML(basePath + "rank1.png", 16, 16) + " ";
                
            case 2: 
                // return createImageHTML(basePath + "Silver.png", 16, 16) + " ";
                return "2nd ";
                
            case 3: 
                // return createImageHTML(basePath + "Bronze.png", 16, 16) + " ";
                return "3rd ";
                
            default: 
                return "   ";
        }
    }
    
    // Helper method to create HTML for images in text components
    private String createImageHTML(String imagePath, int width, int height) {
        try {
            // Convert file path to URL for HTML
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                String imageUrl = imageFile.toURI().toString();
                return "<img src='" + imageUrl + "' width='" + width + "' height='" + height + "'>";
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
        }
        return ""; // Return empty string if image fails to load
    }

    private String getMotivationalMessage(String badgeName) {
        switch (badgeName.toLowerCase()) {
            case "gold":
                return "Excellent work! You've mastered mental health awareness!";
            case "silver":
                return "Great job! You have solid understanding!";
            case "bronze":
                return "Good effort! You're on the right track!";
            case "keep learning":
                return "Every journey starts with a step. Keep learning!";
            default:
                return "Thank you for participating!";
        }
    }

    private Component getComponentByName(String name) {
        for (Component comp : mainPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(name)) {
                return comp;
            }
        }
        // Fallback: find by card layout
        Component[] components = mainPanel.getComponents();
        switch (name) {
            case "results": return components.length > 2 ? components[2] : null;
            case "gamification": return components.length > 3 ? components[3] : null;
            case "leaderboard": return components.length > 4 ? components[4] : null;
            default: return null;
        }
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