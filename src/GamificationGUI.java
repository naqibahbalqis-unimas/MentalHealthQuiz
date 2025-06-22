/**
 * Class: GamificationGUI
 * Creator: Siti Norlie Yana
 * Matric No: 101059
 * Tester: 
 * Date: 18/6/2025
 *
 * Description:
 * This class builds the graphical user interface for the mental health gamification system.
 * Updated to use persistent leaderboard data that survives application restarts.
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class GamificationGUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private GamificationEngine engine = new GamificationEngine();
    private User currentUser;

    public GamificationGUI(User user, int correctAnswers) {
        this.currentUser = user;
        engine.addUser(user);
        engine.awardPointsToUser(user, correctAnswers);

        frame = new JFrame("Mental Health Awareness Gamification");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null); // Center the window

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(buildResultPanel(), "result");
        mainPanel.add(buildLeaderboardPanel(), "leaderboard");

        cardLayout.show(mainPanel, "result");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("🎉 Congratulations! 🎉", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Badge image
        JLabel badgeImage = new JLabel();
        badgeImage.setHorizontalAlignment(SwingConstants.CENTER);
        badgeImage.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Result text area
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        resultArea.setBackground(panel.getBackground());
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(panel.getBackground());
        
        JButton viewLeaderboardBtn = new JButton("🏆 View Leaderboard");
        viewLeaderboardBtn.setFont(new Font("Times New Roman", Font.BOLD, 14));
        
        JButton takeQuizAgainBtn = new JButton("🔄 Take Quiz Again");
        takeQuizAgainBtn.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        
        JButton exitBtn = new JButton("❌ Exit");
        exitBtn.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        buttonPanel.add(viewLeaderboardBtn);
        buttonPanel.add(takeQuizAgainBtn);
        buttonPanel.add(exitBtn);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(panel.getBackground());
        contentPanel.add(badgeImage, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Update content when panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateResultContent(resultArea, badgeImage);
            }
        });

        // Button actions
        viewLeaderboardBtn.addActionListener(e -> {
            updateLeaderboardContent(); // Refresh leaderboard before showing
            cardLayout.show(mainPanel, "leaderboard");
        });
        
        takeQuizAgainBtn.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(QuizAppGUI::new);
        });
        
        exitBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                frame, 
                "Are you sure you want to exit?", 
                "Exit Confirmation", 
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        return panel;
    }

    private void updateResultContent(JTextArea resultArea, JLabel badgeImage) {
        resultArea.setText("");
        StringBuilder sb = new StringBuilder();
        sb.append("🎓 QUIZ RESULTS\n");
        sb.append("═".repeat(40)).append("\n\n");
        sb.append("👤 Name: ").append(currentUser.getName()).append("\n\n");
        sb.append("⭐ Points Earned: ").append(currentUser.getTotalPoints()).append(" points\n\n");
        sb.append("🏅 Badge Achieved: ").append(currentUser.getBadgeName()).append("\n\n");
        
        // Add badge description
        if (currentUser.getBadge() != null) {
            sb.append("📋 Badge Requirements: ").append(currentUser.getBadge().getRequirement()).append("\n\n");
        }
        
        // Add motivational message based on badge
        String motivation = getMotivationalMessage(currentUser.getBadgeName());
        sb.append("💬 ").append(motivation);
        
        resultArea.setText(sb.toString());

        // Display badge image
        String badgeIconPath = currentUser.getBadgeIconPath();
        if (badgeIconPath != null && !badgeIconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(badgeIconPath);
                Image img = icon.getImage();
                
                // Get original dimensions
                int originalWidth = img.getWidth(null);
                int originalHeight = img.getHeight(null);
                
                if (originalWidth > 0 && originalHeight > 0) {
                    // Calculate scaled dimensions maintaining aspect ratio
                    int maxSize = 150;
                    double ratio = Math.min((double) maxSize / originalWidth, (double) maxSize / originalHeight);
                    
                    int scaledWidth = (int) (originalWidth * ratio);
                    int scaledHeight = (int) (originalHeight * ratio);
                    
                    Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    badgeImage.setIcon(new ImageIcon(scaledImg));
                }
            } catch (Exception ex) {
                badgeImage.setText("Badge: " + currentUser.getBadgeName());
                badgeImage.setFont(new Font("Times New Roman", Font.BOLD, 16));
            }
        }
    }

    private String getMotivationalMessage(String badgeName) {
        switch (badgeName.toLowerCase()) {
            case "gold":
                return "Excellent work! You've mastered mental health awareness! 🌟";
            case "silver":
                return "Great job! You have a solid understanding of mental health concepts! 👏";
            case "bronze":
                return "Good effort! You're on the right track to learning more! 📚";
            case "keep learning":
                return "Every journey starts with a single step. Keep learning and growing! 🌱";
            default:
                return "Thank you for participating in mental health awareness! 💙";
        }
    }

    private JPanel buildLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 255, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title with banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(panel.getBackground());
        
        JLabel titleLabel = new JLabel("🏆 LEADERBOARD 🏆", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel bannerLabel = new JLabel();
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String bannerPath = "assets" + File.separator + "badges" + File.separator + "Leaderboard.png";
        try {
            ImageIcon bannerIcon = new ImageIcon(bannerPath);
            Image img = bannerIcon.getImage();
            
            // Get original dimensions
            int originalWidth = img.getWidth(null);
            int originalHeight = img.getHeight(null);
            
            if (originalWidth > 0 && originalHeight > 0) {
                // Calculate scaled dimensions maintaining aspect ratio
                int maxWidth = 300;
                int maxHeight = 150;
                
                double widthRatio = (double) maxWidth / originalWidth;
                double heightRatio = (double) maxHeight / originalHeight;
                double ratio = Math.min(widthRatio, heightRatio);
                
                int scaledWidth = (int) (originalWidth * ratio);
                int scaledHeight = (int) (originalHeight * ratio);
                
                Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                bannerLabel.setIcon(new ImageIcon(scaledImg));
            }
        } catch (Exception e) {
            bannerLabel.setText("🏆 LEADERBOARD 🏆");
            bannerLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        }

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(bannerLabel, BorderLayout.CENTER);

        // Leaderboard content
        JTextArea leaderboardArea = new JTextArea();
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        leaderboardArea.setBackground(Color.WHITE);
        leaderboardArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(leaderboardArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Rankings"));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(panel.getBackground());
        
        JButton backBtn = new JButton("⬅️ Back to Result");
        backBtn.setFont(new Font("Times New Roman", Font.BOLD, 14));
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        buttonPanel.add(backBtn);
        buttonPanel.add(refreshBtn);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Store reference for updates
        panel.putClientProperty("leaderboardArea", leaderboardArea);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "result"));
        refreshBtn.addActionListener(e -> updateLeaderboardContent());

        return panel;
    }

    private void updateLeaderboardContent() {
        JPanel leaderboardPanel = null;
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel) {
                Object property = ((JPanel) comp).getClientProperty("leaderboardArea");
                if (property != null) {
                    leaderboardPanel = (JPanel) comp;
                    break;
                }
            }
        }
        
        if (leaderboardPanel == null) return;
        
        JTextArea leaderboardArea = (JTextArea) leaderboardPanel.getClientProperty("leaderboardArea");
        if (leaderboardArea == null) return;

        leaderboardArea.setText("");
        
        // Get users from the gamification engine (persistent data)
        List<User> allUsers = engine.getAllUsers();
        
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append(String.format("%-5s %-20s %-10s %-15s%n", "Rank", "Name", "Points", "Badge"));
        sb.append("─".repeat(60)).append("\n");
        
        // User rows with ranking symbols
        int rank = 1;
        for (User u : allUsers) {
            String rankSymbol = getRankSymbol(rank);
            sb.append(String.format("%-5s %-20s %-10d %-15s%n",
                rankSymbol + rank, 
                u.getName().length() > 18 ? u.getName().substring(0, 18) + ".." : u.getName(),
                u.getTotalPoints(), 
                u.getBadgeName()
            ));
            rank++;
        }
        
        if (allUsers.isEmpty()) {
            sb.append("\n").append(" ".repeat(20)).append("No users yet!");
        }
        
        leaderboardArea.setText(sb.toString());
    }

    private String getRankSymbol(int rank) {
        switch (rank) {
            case 1: return "🥇 ";
            case 2: return "🥈 ";
            case 3: return "🥉 ";
            default: return "   ";
        }
    }

    public static void launchFromQuiz(User user, int correctAnswers) {
        SwingUtilities.invokeLater(() -> new GamificationGUI(user, correctAnswers));
    }

    // Method for testing multiple users
    public static void simulateMultipleUsers() {
        SwingUtilities.invokeLater(() -> {
            // Create some sample users with different scores
            new GamificationGUI(new User("Alice", 18), 9);
            new GamificationGUI(new User("Bob", 12), 6);
            new GamificationGUI(new User("Charlie", 8), 4);
            new GamificationGUI(new User("Diana", 4), 2);
        });
    }
}