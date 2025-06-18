import java.util.List;

public interface DatabaseHandler {
    void saveData(String data) throws DataAccessException;
    String loadData() throws DataAccessException;
    List<Integer> getUserScores() throws DataAccessException;
    void appendScore(int score) throws DataAccessException;
    void deleteData() throws DataAccessException;
}
