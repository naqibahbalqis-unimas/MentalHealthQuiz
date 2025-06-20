
/**
 * Class: User
 * Creator: Siti Norlie Yana
 * Matric No: 101059
 * Tester: 
 * Date: 18/6/2025
 * Description: This interface defines the contract for any reward-related logic in the
 * gamification engine. Implementing classes must provide methods for awarding
 * points, retrieving total points, getting badge status, and updating the leaderboard.
 */

 public class User {
    private String name;
    private int totalPoints;
    private Badge badge;

    // Default constructor (starts at 0 points)
    public User(String name) {
        this.name = name;
        this.totalPoints = 0;
    }

    // Overloaded constructor (custom starting points)
    public User(String name, int points) {
        this.name = name;
        this.totalPoints = points;
    }
    public void awardPoints(int points) {
        this.totalPoints += points;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public Badge getBadge() {
        return badge;
    }

    public String getBadgeName() {
        return badge != null ? badge.getBadgeName() : "None";
    }

    public String getBadgeIconPath() {
        return badge != null ? badge.getBadgeIconPath() : "";
    }

    public String getName() {
        return name;
    }
}