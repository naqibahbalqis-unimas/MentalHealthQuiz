/**
 * Interface: RewardSystem
 * Creator: Siti Norlie Yana
 * Matric No: 101059
 * Tester: 
 * Date: 18/6/2025
 * Description: This interface defines the contract for any reward-related logic in the
 * gamification engine. Implementing classes must provide methods for awarding
 * points, retrieving total points, getting badge status, and updating the leaderboard.
 */

public interface RewardSystem {
    void awardPoints(int points);
    int getTotalPoints();
    String getBadge();
    void updateLeaderboard();
}
