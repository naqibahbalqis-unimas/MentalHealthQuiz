
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
    /**
     * Award a number of points.
     *
     * @param points points to add
     */
    void awardPoints(int points);

    /**
     * @return total accumulated points
     */
    int getTotalPoints();

    /**
     * Retrieve the badge earned by the user.
     *
     * @return the current {@link Badge}
     */
    Badge getBadge();
}
