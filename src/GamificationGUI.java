
/**
 * Class: GamificationGUI
 * Creator: Siti Norlie Yana
 * Matric No: 101059
 * Tester: 
 * Date: 18/6/2025
 *
 * Description:
 * This class builds the graphical user interface for the mental health gamification system.
 * It shows the quiz result screen (with points and badges), and a leaderboard showing all users.
 * This GUI connects with QuizModule and supports simulation of multiple users.
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class GamificationGUI {
    private static List<User> allUsers = new ArrayList<>(); // Global user list for leaderboard

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private GamificationEngine engine = new GamificationEngine();
    private User currentUser;

    public GamificationGUI(User user, int correctAnswers) {
        this.currentUser = user;
        engine.addUser(user);
        allUsers.add(user);
        engine.awardPointsToUser(user, correctAnswers);

        frame = new JFrame("Mental Health Awareness Gamification");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

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

        JLabel badgeImage = new JLabel();
        badgeImage.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JButton viewLeaderboardBtn = new JButton("View Leaderboard");

        panel.add(badgeImage, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(viewLeaderboardBtn, BorderLayout.SOUTH);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                resultArea.setText("");
                StringBuilder sb = new StringBuilder();
                sb.append("✅ Quiz Completed\n\n");
                sb.append("Name: ").append(currentUser.getName()).append("\n");
                sb.append("Points: ").append(currentUser.getTotalPoints()).append("\n");
                sb.append("Badge: ").append(currentUser.getBadgeName()).append("\n");
                resultArea.setText(sb.toString());

                ImageIcon icon = new ImageIcon(currentUser.getBadgeIconPath());
                Image img = icon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                badgeImage.setIcon(new ImageIcon(img));
            }
        });

        viewLeaderboardBtn.addActionListener(e -> cardLayout.show(mainPanel, "leaderboard"));

        return panel;
    }

    private JPanel buildLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 255, 250));

        JLabel bannerLabel = new JLabel();
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String bannerPath = "assets" + File.separator + "badges" + File.separator + "Leaderboard.png";
        ImageIcon bannerIcon = new ImageIcon(bannerPath);
        Image bannerImg = bannerIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        bannerLabel.setIcon(new ImageIcon(bannerImg));

        JTextArea leaderboardArea = new JTextArea();
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JButton backBtn = new JButton("Back to Result");

        panel.add(bannerLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(leaderboardArea), BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);

        leaderboardArea.setText("");
        allUsers.sort((u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append(String.format("%-5s %-15s %-10s %-15s%n", "No", "Name", "Points", "Badge"));
        sb.append("-----------------------------------------------------\n");
        
        // User rows
        int rank = 1;
        for (User u : allUsers) {
            sb.append(String.format("%-5d %-15s %-10d %-15s%n",
            rank++, u.getName(), u.getTotalPoints(), u.getBadgeName()));
        }
        leaderboardArea.setText(sb.toString());

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "result"));

        return panel;
    }

    public static void launchFromQuiz(User user, int correctAnswers) {
        SwingUtilities.invokeLater(() -> new GamificationGUI(user, correctAnswers));
    }


}
