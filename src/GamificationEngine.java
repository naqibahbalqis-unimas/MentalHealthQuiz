import java.io.File;
import java.util.*;

/**
 * Class: GamificationEngine
 * Creator: Siti Norlie Yana
 * Matric No: 101059
 * Tester: 
 * Date: 18/6/2025
 * Description: This class implements the core logic of the gamification system. It handles:
 * - Tracking users and awarding points based on quiz results
 * - Assigning badges according to predefined thresholds
 * - Maintaining and sorting the leaderboard with persistent storage
 *
 * Updated to ensure leaderboard data persists between application sessions.
 */

public class GamificationEngine implements RewardSystem {
    private List<User> users = new ArrayList<>(); // stores all users who have taken the quiz
    private List<Badge> availableBadges = new ArrayList<>(); // stores the badges the system can assign
    private DatabaseHandler dataManager;
    private String leaderboardFile;

    public GamificationEngine() {
        this("leaderboard.txt");
    }

    public GamificationEngine(String dataFile) {
        this.leaderboardFile = dataFile;
        
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
     * Load leaderboard data from the file and populate the users list.
     * This ensures previous quiz attempts persist between application sessions.
     * Format: rank,name,points
     */
    private void loadLeaderboard() {
        if (dataManager == null) return;

        try {
            String data = dataManager.loadData();
            if (data == null || data.trim().isEmpty()) {
                System.out.println("No existing leaderboard data found.");
                return;
            }

            String[] lines = data.split("\n");
            users.clear();
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        // Skip rank (parts[0]), get name and points
                        String name = parts[1].trim();
                        int points = Integer.parseInt(parts[2].trim());
                        
                        // Check if user already exists (avoid duplicates)
                        User existingUser = findUserByName(name);
                        if (existingUser == null) {
                            User user = new User(name, points);
                            assignBadge(user);
                            users.add(user);
                            System.out.println("Loaded user: " + name + " with " + points + " points");
                        } else {
                            // Update existing user with higher score if applicable
                            if (points > existingUser.getTotalPoints()) {
                                existingUser.awardPoints(points - existingUser.getTotalPoints());
                                assignBadge(existingUser);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid points format in line: " + line);
                    }
                }
            }
            
            // Sort users by points (descending order)
            sortUsersByPoints();
            System.out.println("Loaded " + users.size() + " users from leaderboard.");
            
        } catch (DataAccessException e) {
            System.err.println("Failed to load leaderboard: " + e.getMessage());
        }
    }

    /**
     * Find a user by name (case-insensitive)
     */
    private User findUserByName(String name) {
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Add a new user or update existing user's score
     */
    public void addUser(User user) {
        User existingUser = findUserByName(user.getName());
        if (existingUser == null) {
            users.add(user);
            System.out.println("Added new user: " + user.getName());
        } else {
            // Update existing user's score if the new score is higher
            if (user.getTotalPoints() > existingUser.getTotalPoints()) {
                existingUser.awardPoints(user.getTotalPoints() - existingUser.getTotalPoints());
                assignBadge(existingUser);
                System.out.println("Updated user: " + user.getName() + " with new score: " + existingUser.getTotalPoints());
            }
        }
    }

    public void awardPointsToUser(User user, int correctAnswers) {
        int points = correctAnswers * 2;
        user.awardPoints(points);
        assignBadge(user);
        updateLeaderboard();
        
        System.out.println("Awarded " + points + " points to " + user.getName() + 
                         ". Total: " + user.getTotalPoints() + " points. Badge: " + user.getBadgeName());
    }

    private void assignBadge(User user) {
        // Find the highest badge the user qualifies for
        Badge bestBadge = null;
        for (Badge badge : availableBadges) {
            if (badge.checkRequirement(user.getTotalPoints())) {
                if (bestBadge == null || badge.getRequirementPoints() > bestBadge.getRequirementPoints()) {
                    bestBadge = badge;
                }
            }
        }
        if (bestBadge != null) {
            user.setBadge(bestBadge);
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
     * Sort users by their total points in descending order and persist to file.
     */
    public void updateLeaderboard() {
        sortUsersByPoints();
        persistLeaderboard();
    }

    /**
     * Sort users by points (descending order)
     */
    private void sortUsersByPoints() {
        users.sort((u1, u2) -> {
            // Primary sort: by points (descending)
            int pointComparison = Integer.compare(u2.getTotalPoints(), u1.getTotalPoints());
            if (pointComparison != 0) {
                return pointComparison;
            }
            // Secondary sort: by name (ascending) for ties
            return u1.getName().compareToIgnoreCase(u2.getName());
        });
    }

    public void showLeaderboard() {
        System.out.println("\n🏆 Final Leaderboard:");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". " + user.getName() + " - " + 
                             user.getTotalPoints() + " pts - Badge: " + user.getBadgeName());
        }
    }

    /**
     * Persist the current leaderboard ordering to the database file.
     * Format: rank,name,points
     */
    private void persistLeaderboard() {
        if (dataManager == null) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            sb.append(i + 1)  // rank (1-based)
              .append(",")
              .append(user.getName())
              .append(",")
              .append(user.getTotalPoints())
              .append("\n");
        }

        try {
            dataManager.saveData(sb.toString());
            System.out.println("Leaderboard saved successfully with " + users.size() + " users.");
        } catch (DataAccessException e) {
            System.err.println("Failed to save leaderboard: " + e.getMessage());
        }
    }

    /**
     * Get all users sorted by points (descending)
     */
    public List<User> getUsers() {
        sortUsersByPoints();
        return new ArrayList<>(users); // Return a copy to prevent external modifications
    }

    /**
     * Get all users from leaderboard (for external access)
     */
    public List<User> getAllUsers() {
        return getUsers();
    }

    /**
     * Clear all leaderboard data
     */
    public void clearLeaderboard() {
        users.clear();
        if (dataManager != null) {
            try {
                dataManager.deleteData();
                System.out.println("Leaderboard cleared successfully.");
            } catch (DataAccessException e) {
                System.err.println("Failed to clear leaderboard: " + e.getMessage());
            }
        }
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