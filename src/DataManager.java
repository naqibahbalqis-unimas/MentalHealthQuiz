/**
 * Class: DataManager
 * Creator: Rawaidatul Aliah binti Mohd Rawawi
 * Matric No: 97787
 * Tester: 
 * Date: 18/6/2025
 * Description: This class manages file-based storage for user quiz scores.
 * It implements the DatabaseHandler interface to:
 * - Save and load data to/from a text file
 * - Append new scores with validation
 * - Calculate average and latest scores
 * - Provide motivational messages based on performance
 * - Delete/reset stored data
 *
 * It supports error handling using the custom DataAccessException
 * and plays a central role in maintaining score data for gamification and analysis.
 */

import java.io.*;
import java.util.*;

public class DataManager implements DatabaseHandler {
    private String fileName;
    private List<Integer> scores;

    /**
     * Constructor that initializes the DataManager with a filename.
     * Creates the file if it does not exist.
     */
    public DataManager(String fileName) throws DataAccessException {
        this.fileName = fileName;
        this.scores = new ArrayList<>();
        initializeFile();
    }

    /**
     * Overloaded constructor that accepts initial scores.
     */
    public DataManager(String fileName, List<Integer> initialScores) throws DataAccessException {
        this.fileName = fileName;
        this.scores = new ArrayList<>(initialScores);
        initializeFile();
    }

    /**
     * Checks if the file exists. If not, creates a new one.
     */
    private void initializeFile() throws DataAccessException {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not create data file", e);
        }
    }

    /**
     * Saves the given string data to the file, overwriting previous content.
     */
    @Override
    public void saveData(String data) throws DataAccessException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(data);
        } catch (IOException e) {
            throw new DataAccessException("Failed to save data.", e);
        }
    }

    /**
     * Loads the full contents of the file as a single string.
     */
    @Override
    public String loadData() throws DataAccessException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load data.", e);
        }
        return sb.toString();
    }

    /**
     * Loads user scores from the file into the scores list.
     * Skips invalid entries.
     */
    @Override
    public List<Integer> getUserScores() throws DataAccessException {
        scores.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    scores.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    // Ignore invalid entries
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Unable to read scores from file.", e);
        }
        return scores;
    }

    /**
     * Appends a new score to the file and updates the internal scores list.
     * Score must be between 0 and 100.
     */
    @Override
    public void appendScore(int score) throws DataAccessException {
        if (score < 0 || score > 100) {
            throw new DataAccessException("Score must be between 0 and 100");
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(score + "\n");
        } catch (IOException e) {
            throw new DataAccessException("Failed to append score.", e);
        }
        scores.add(score);
    }

    /**
     * Returns the most recent score from the file.
     * Returns 0 if no scores exist.
     */
    public int getLatestScore() throws DataAccessException {
        getUserScores(); // Ensure latest data
        return scores.isEmpty() ? 0 : scores.get(scores.size() - 1);
    }

    /**
     * Calculates and returns the average score.
     * Returns 0.0 if no scores are available.
     */
    public double getAverageScore() throws DataAccessException {
        getUserScores();
        if (scores.isEmpty()) return 0.0;
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return (double) sum / scores.size();
    }

     /**
     * Returns a motivational message based on the average score.
     */
    public String getMotivationalMessage() throws DataAccessException {
        double avg = getAverageScore();
        if (avg >= 80) return "Outstanding!";
        if (avg >= 60) return "That's good!";
        if (avg >= 40) return "Good try!";
        if (avg >= 20) return "You can do better!";
        return "Don't give up!";
    }

    /**
     * Deletes all data in the file and clears the internal scores list.
     */
    @Override
    public void deleteData() throws DataAccessException {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.print("");
            scores.clear();
        } catch (IOException e) {
            throw new DataAccessException("Failed to delete data.", e);
        }
    }
}
