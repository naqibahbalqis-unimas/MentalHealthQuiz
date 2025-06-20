

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

    public GamificationEngine() {
        // Use File.separator for cross-platform compatibility
        String basePath = "assets" + File.separator + "badges" + File.separator;
        availableBadges.add(new Badge("Gold", basePath + "Gold.png", 15, "Score 15+ points"));
        availableBadges.add(new Badge("Silver", basePath + "Silver.png", 10, "Score 10–14 points"));
        availableBadges.add(new Badge("Bronze", basePath + "Bronze.png", 5, "Score 5–9 points"));
        availableBadges.add(new Badge("Keep Learning", basePath + "Keep_Learning.png", 0, "Less than 5 points"));
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void awardPointsToUser(User user, int correctAnswers) {
        int points = correctAnswers * 2;
        user.awardPoints(points);
        assignBadge(user);
        updateLeaderboard();
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
    }

    public void showLeaderboard() {
        System.out.println("\n🏆 Final Leaderboard:");
        for (User u : users) {
            System.out.println(u.getName() + " - " + u.getTotalPoints() + " pts - Badge: " + u.getBadgeName());
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
