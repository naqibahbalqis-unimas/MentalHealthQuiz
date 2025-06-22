

import java.io.File;
/**
 * Class: GamificationEngine
 * Creator: Siti Norlie Yana
 * Matric No: 101059
 * Tester: 
 * Date: 18/6/2025
 * Description: This class implements the core logic of the gamification system. It handles:
 * - Tracking users and awarding points based on quiz results
 * - Assigning badges according to predefined thresholds
 * - Maintaining and sorting the leaderboard
 *
 * It implements the RewardSystem interface to ensure standardized methods
 * for awarding points, accessing leaderboard and badge functionality.
 *
 * The engine supports extensibility by storing available badges in a list
 * and assigning them dynamically based on user performance.
 */

import java.util.*;

public class GamificationEngine implements RewardSystem {
    private List<User> users = new ArrayList<>(); // stores all users who have taken the quiz
    private List<Badge> availableBadges = new ArrayList<>(); // stores the badges the system can assign (Gold, Silver, Bronze, Keep Learning.)
    private DatabaseHandler dataManager;

    public GamificationEngine() {
        this("scores.txt");
    }

    public GamificationEngine(String dataFile) {
        // Use File.separator for cross-platform compatibility
        String basePath = "assets" + File.separator + "badges" + File.separator;
        availableBadges.add(new Badge("Gold", basePath + "Gold.png", 15, "Score 15+ points"));
        availableBadges.add(new Badge("Silver", basePath + "Silver.png", 10, "Score 10–14 points"));
        availableBadges.add(new Badge("Bronze", basePath + "Bronze.png", 5, "Score 5–9 points"));
        availableBadges.add(new Badge("Keep Learning", basePath + "Keep_Learning.png", 0, "Less than 5 points"));

        try {
            dataManager = new DataManager(dataFile);
            loadLeaderboard();
        } catch (DataAccessException e) {
            System.err.println("Could not initialize data manager: " + e.getMessage());
        }
    }

    /**
     * Load leaderboard data from the underlying file and populate the users list.
     * This ensures previous quiz attempts persist between application sessions.
     */
    private void loadLeaderboard() {
        if (dataManager == null) return;

        try {
            String data = dataManager.loadData();
            if (data == null || data.isEmpty()) return;

            String[] lines = data.split("\n");
            users.clear();
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                try {
                    String name = parts[1].trim();
                    int points = Integer.parseInt(parts[2].trim());
                    User u = new User(name, points);
                    assignBadge(u);
                    users.add(u);
                } catch (NumberFormatException ignore) {
                    // Skip invalid lines
                }
            }
            users.sort((u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());
        } catch (DataAccessException e) {
            System.err.println("Failed to load leaderboard: " + e.getMessage());
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void awardPointsToUser(User user, int correctAnswers) {
        int points = correctAnswers * 2;
        user.awardPoints(points);
        assignBadge(user);
        updateLeaderboard();

        // Persist the user's score if a data manager is available
        if (dataManager != null) {
            try {
                dataManager.appendScore(user.getTotalPoints());
            } catch (DataAccessException e) {
                System.err.println("Failed to save score: " + e.getMessage());
            }
        }
    }

    private void assignBadge(User user) {
        for (Badge badge : availableBadges) {
            if (badge.checkRequirement(user.getTotalPoints())) {
                user.setBadge(badge);
                break;
            }
        }
    }

    public void assignBadges() {
        for (User user : users) {
            assignBadge(user);
        }
    }

    @Override
    public int getTotalPoints() {
        return users.stream().mapToInt(User::getTotalPoints).sum();
    }

    /**
     * Sort users by their total points in descending order.
     */
    public void updateLeaderboard() {
        users.sort((u1, u2) -> u2.getTotalPoints() - u1.getTotalPoints());
        persistLeaderboard();
    }

    public void showLeaderboard() {
        System.out.println("\n🏆 Final Leaderboard:");
        for (User u : users) {
            System.out.println(u.getName() + " - " + u.getTotalPoints() + " pts - Badge: " + u.getBadgeName());
        }
    }

    /**
     * Persist the current leaderboard ordering to the database file.
     */
    private void persistLeaderboard() {
        if (dataManager == null) return;

        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (User u : users) {
            sb.append(rank++)
              .append(",")
              .append(u.getName())
              .append(",")
              .append(u.getTotalPoints())
              .append("\n");
        }

        try {
            dataManager.saveData(sb.toString());
        } catch (DataAccessException e) {
            System.err.println("Failed to save leaderboard: " + e.getMessage());
        }
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public void awardPoints(int points) {
        // Award points to the current user if needed
        if (!users.isEmpty()) {
            users.get(users.size() - 1).awardPoints(points);
        }
    }

    @Override
    public Badge getBadge() {
        // Return the badge of the current user or null if no users exist
        if (!users.isEmpty()) {
            return users.get(users.size() - 1).getBadge();
        }
        return null;
    }
}
