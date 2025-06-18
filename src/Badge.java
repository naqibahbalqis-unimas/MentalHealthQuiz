
/**
 * Class: Badge
 * Creator: Siti Norlie Yana
 * Matric No: 101059
 * Tester: 
 * Date: 18/6/2025
 * Description: This class represents a digital badge in the gamification system.
 * Each badge has:
 * - a name (e.g., Gold, Silver)
 * - an icon image path
 * - a point requirement (minimum score to earn it)
 * It is used by GamificationEngine to assign the appropriate badge to each user.
 */

 public class Badge {
    private String badgeName;
    private String badgeIconPath;
    private String requirement;
    private int requirementPoints;
    private String name;


    public Badge(String badgeName, String badgeIconPath, int requirementPoints, String requirement) {
        this.badgeName = badgeName;
        this.badgeIconPath = badgeIconPath;
        this.requirementPoints = requirementPoints;
        this.requirement = requirement;
    }

    public boolean checkRequirement(int userPoints) {
        return userPoints >= requirementPoints;
    }

    public String getBadgeInfo() {
        return badgeName + " - Requires: " + requirement;
    }

    public void displayBadge() {
        System.out.println(getBadgeInfo());
    }

    public String getBadgeName() {
        return badgeName;
    }

    public String getBadgeIconPath() {
        return badgeIconPath;
    }

    public String getRequirement() {
        return requirement;
    }

    public int getRequirementPoints() {
        return requirementPoints;
    }
     public String getName() {
        return name;
    }
}
