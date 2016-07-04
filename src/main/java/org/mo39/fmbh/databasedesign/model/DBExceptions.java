package org.mo39.fmbh.databasedesign.model;

/**
 * Database Design Exceptions.
 *
 * @author Jihan Chen
 *
 */
@SuppressWarnings("serial")
public class DBExceptions extends Exception {

  /**
   * Print the error stack track and give it a fast-fail error. This function is used when an
   * exceptions like IOException is thrown during execution. This project is not supposed to do
   * log-back or handle these native Exceptions. Hopefully IOExceptions won't happen. But when it
   * happens, it's possible the data in the databse is badly damaged. For example, the funtion
   * writing records to DB is interrupted and the data in the file then become a mess, which can no
   * longer be read correctly and requires a fix manually.
   *
   * @param e
   */
  public static void newError(Throwable e) {
    e.printStackTrace();
    throw new Error();
  }

  /**
   * Print the error message and give it a fast-fail error.
   *
   * @param e
   */
  public static void newError(String e) {
    throw new Error(e);
  }

  public DBExceptions() {}

  public DBExceptions(String description) {
    super(description);
  }

  /**
   * Indicates a bad usage for input command.
   *
   * @author Jihan Chen
   *
   */
  public static class BadUsageException extends DBExceptions {

    private static final long serialVersionUID = 1L;

    public BadUsageException() {}

    public BadUsageException(String description) {
      super(description);
    }
  }

  /**
   * Indicates that there are more than one primary assigned when creating table.
   * 
   * @author Jihan Chen
   *
   */
  public static class DuplicatePrimaryKeyException extends DBExceptions {
    private static final long serialVersionUID = 1L;

    public DuplicatePrimaryKeyException() {}

    public DuplicatePrimaryKeyException(String description) {
      super(description);
    }
  }

  /**
   * Indicates the insert record does not observe the constraint.
   *
   * @author Jihan Chen
   *
   */
  public static class ConstraintViolationException extends DBExceptions {
    private static final long serialVersionUID = 1L;

    public ConstraintViolationException() {}

    public ConstraintViolationException(String description) {
      super(description);
    }
  }

  /**
   * Indicates that the column name in the where clause is not found in DB.
   * 
   * @author Jihan Chen
   *
   */
  public static class ColumnNameNotFoundException extends DBExceptions {
    private static final long serialVersionUID = 1L;

    public ColumnNameNotFoundException() {}

    public ColumnNameNotFoundException(String description) {
      super(description);
    }
  }

  /**
   * Indicates that the data type cannot be recognized or is not supported in current design.
   *
   * @author Jihan Chen
   *
   */
  public static class UnrecognizableDataTypeException extends DBExceptions {

    private static final long serialVersionUID = 1L;

    public UnrecognizableDataTypeException() {}

    public UnrecognizableDataTypeException(String description) {
      super(description);
    }
  }

  /**
   * Indicates that the constraint cannot be recognized or is not supported in current design.
   *
   * @author Jihan Chen
   *
   */
  public static class UnrecognizableConstraintException extends DBExceptions {

    private static final long serialVersionUID = 1L;

    public UnrecognizableConstraintException() {}

    public UnrecognizableConstraintException(String description) {
      super(description);
    }

  }

  /**
   * Indicates that the folders and tables in archive is not consistent with the information
   * provided by Information_Schema.
   *
   * @author Jihan Chen
   *
   */
  public static class InvalidInformationSchemaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidInformationSchemaException() {}

    public InvalidInformationSchemaException(String description) {
      super(description);
    }
  }

  /**
   * Indicates that there is an error when adding new record. For example, the length of values is
   * not consistent with the number of the columns.
   *
   * @author Jihan Chen
   *
   */
  public static class AddRecordException extends DBExceptions {
    private static final long serialVersionUID = 1L;

    public AddRecordException() {}

    public AddRecordException(String description) {
      super(description);
    }
  }

  /**
   * Warp a ClassNotFoundException to DBExceptions. This should be used when select record from
   * database.
   *
   * @author Jihan Chen
   *
   */
  public static class ClassNotFound extends DBExceptions {
    private static final long serialVersionUID = 1L;

    public ClassNotFound(ClassNotFoundException e) {
      super(e.getMessage());
    }
  }

}
