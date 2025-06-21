import java.util.List;

/**
 * Interface: DatabaseHandler
 * Creator: Rawaidatul Aliah binti Mohd Rawawi
 * Matric No: 97787
 * Tester: 
 * Date: 18/6/2025
 * Description: This interface defines a standard contract for managing data persistence 
 * within the application. It is implemented by the DataManager class and 
 * includes core methods for:
 * - Saving and loading string-based data
 * - Managing user score entries
 * - Appending validated scores
 * - Resetting stored data when required
 * 
 * This promotes abstraction and flexibility by allowing different storage 
 * mechanisms without affecting other modules.
 */
public interface DatabaseHandler {

    /**
     * Saves the provided data to the storage, overwriting existing content.
     * 
     * @param data The string data to be saved.
     * @throws DataAccessException if saving fails due to I/O error.
     */
    void saveData(String data) throws DataAccessException;

    /**
     * Loads and returns the full data as a string from storage.
     * 
     * @return The loaded string content.
     * @throws DataAccessException if loading fails due to I/O error.
     */
    String loadData() throws DataAccessException;

    /**
     * Retrieves a list of user scores (as integers) from storage.
     * 
     * @return List of scores parsed from file.
     * @throws DataAccessException if reading or parsing fails.
     */
    List<Integer> getUserScores() throws DataAccessException;

    /**
     * Appends a new validated score to the storage.
     * 
     * @param score The score to append (should be between 0 and 100).
     * @throws DataAccessException if the score is invalid or writing fails.
     */
    void appendScore(int score) throws DataAccessException;

    /**
     * Deletes or clears all stored data (e.g., resets file content).
     * 
     * @throws DataAccessException if deletion fails.
     */
    void deleteData() throws DataAccessException;
}
