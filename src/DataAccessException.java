/**
 * Class: DataAccessException
 * Creator: Rawaidatul Aliah binti Mohd Rawawi
 * Matric No: 97787
 * Tester: 
 * Date: 18/6/2025
 * Description: This class defines a custom checked exception used for handling
 * data access errors in the application. It is thrown when reading from or 
 * writing to a data source fails due to I/O or parsing issues.
 *
 * By wrapping standard Java exceptions (e.g., IOException), this class provides
 * more meaningful and consistent error reporting across the system.
 *
 * Used by: DataManager, DatabaseHandler, Main
 */
public class DataAccessException extends Exception {

    /**
     * Constructs a new DataAccessException with a custom error message.
     *
     * @param message A description of the exception.
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataAccessException with a message and a cause (another exception).
     *
     * @param message A description of the error.
     * @param cause   The underlying exception that triggered this one.
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
