import java.io.*;
import java.util.*;

public class DataManager implements DatabaseHandler {
    private String fileName;
    private List<Integer> scores;

    public DataManager(String fileName) throws DataAccessException {
        this.fileName = fileName;
        this.scores = new ArrayList<>();
        initializeFile();
    }

    // Overloaded constructor (example of overloading)
    public DataManager(String fileName, List<Integer> initialScores) throws DataAccessException {
        this.fileName = fileName;
        this.scores = new ArrayList<>(initialScores);
        initializeFile();
    }

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

    @Override
    public void saveData(String data) throws DataAccessException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(data);
        } catch (IOException e) {
            throw new DataAccessException("Failed to save data.", e);
        }
    }

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

    public int getLatestScore() throws DataAccessException {
        getUserScores(); // Ensure latest data
        return scores.isEmpty() ? 0 : scores.get(scores.size() - 1);
    }

    public double getAverageScore() throws DataAccessException {
        getUserScores();
        if (scores.isEmpty()) return 0.0;
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return (double) sum / scores.size();
    }

    public String getMotivationalMessage() throws DataAccessException {
        double avg = getAverageScore();
        if (avg >= 80) return "Outstanding!";
        if (avg >= 60) return "That's good!";
        if (avg >= 40) return "Good try!";
        if (avg >= 20) return "You can do better!";
        return "Don't give up!";
    }

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
